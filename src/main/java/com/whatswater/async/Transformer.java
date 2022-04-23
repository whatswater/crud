package com.whatswater.async;


import com.whatswater.async.GenerateClassData.ClassType;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;
import static com.whatswater.async.AsyncConst.*;

/**
 * 1、开发maven插件
 * 2、处理monitor指令
 * 3、处理debug
 *   monitor指令的处理办法1
 *      1. 将方法中的同步标记转换成monitor指令
 *      2. 对monitor enter、exit指令进行匹配和全局编号（使用指令的offset进行编号）
 *      3. 编写支持库，将指令编号当作线程，锁定其他对象
 *      4. 获取对象锁时，返回值为future类型
 *   monitor指令的处理办法2
 *      1. 添加join方法
 *      2. 碰到monitor指令直接编译成线程池执行
 */
// 逐步优化转换方法
public class Transformer {
    private final String path;
    private final ClassReader reader;
    private final ClassNode classNode;
    // 自动生成的access方法计数
    private int accessCount;
    private List<AccessInfo> accessInfoList;

    public Transformer(String path) throws IOException {
        this.path = path;
        this.reader = new ClassReader(path);
        this.classNode = this.readClassNode();
        this.accessCount = 0;
        this.accessInfoList = new ArrayList<>();
    }

    private ClassNode readClassNode() {
        ClassNode classNode = new ClassNode();
        this.reader.accept(classNode, 0);
        return classNode;
    }

    /**
     * 转换class的代码
     * @throws AnalyzerException 原class代码不符合规范
     */
    public List<ClassNameAndData> transform() throws AnalyzerException {
        List<ClassNameAndData> classNameAndDataList = new ArrayList<>();
        List<MethodNode> replaceList = new ArrayList<>();
        for (MethodNode methodNode: classNode.methods) {
            if (TransformerHelper.isAsyncMethod(methodNode)) {
                String suffix = TransformerHelper.nextClassSuffix();
                final boolean needTransformAwait = TransformerHelper.needTransformAwait(methodNode);

                List<GenerateClassData> classDataList = needTransformAwait
                    ? transformNewTaskCall(suffix, methodNode)
                    : transformNewRunnableCall(suffix, methodNode);

                MethodNode replacement = needTransformAwait
                    ? generateCallTaskMethod(classNode, methodNode, classDataList)
                    : generateCallRunnableMethod(classNode, methodNode, classDataList);
                if (replacement == null) {
                    continue;
                }
                replaceList.add(methodNode);
                replaceList.add(replacement);
                for (GenerateClassData classData : classDataList) {
                    ClassNameAndData classNameAndData = new ClassNameAndData();
                    classNameAndData.setClassName(classData.getClassName());
                    classNameAndData.setData(classData.getClassWriter().toByteArray());

                    classNameAndDataList.add(classNameAndData);

                    String name = classData.getClassName();
                    classData.getClassWriter().visitInnerClass(
                        name,
                        classNode.name,
                        name.substring(name.lastIndexOf("$") + 1),
                        ACC_PRIVATE | ACC_STATIC | ACC_FINAL
                    );
                }
            }
        }

        for (int i = 0; i < replaceList.size(); i = i + 2) {
            MethodNode methodNode = replaceList.get(i);
            MethodNode replacement = replaceList.get(i + 1);
            classNode.methods.remove(methodNode);
            replacement.accept(classNode);
        }
        final ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);
        for (AccessInfo accessInfo: accessInfoList) {
            MethodVisitor methodVisitor = cw.visitMethod(ACC_STATIC | ACC_SYNTHETIC, accessInfo.methodName, accessInfo.desc, null, null);
            methodVisitor.visitCode();
            FieldInsnNode fieldInsnNode = accessInfo.fieldInsnNode;
            if (fieldInsnNode != null) {
                switch (fieldInsnNode.getOpcode()) {
                    case GETSTATIC:{
                        methodVisitor.visitFieldInsn(GETSTATIC, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
                        Type type = Type.getType(fieldInsnNode.desc);
                        methodVisitor.visitInsn(type.getOpcode(IRETURN));
                        methodVisitor.visitMaxs(type.getSize(), 0);
                        break;
                    }
                    case GETFIELD:{
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
                        Type type = Type.getType(fieldInsnNode.desc);
                        methodVisitor.visitInsn(type.getOpcode(IRETURN));
                        methodVisitor.visitMaxs(type.getSize(), 1);
                        break;
                    }
                    case PUTSTATIC:{
                        Type type = Type.getType(fieldInsnNode.desc);
                        methodVisitor.visitVarInsn(type.getOpcode(ILOAD), 0);
                        methodVisitor.visitFieldInsn(PUTSTATIC, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
                        methodVisitor.visitInsn(RETURN);
                        methodVisitor.visitMaxs(type.getSize(), type.getSize());
                        break;
                    }
                    case PUTFIELD:{
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        Type type = Type.getType(fieldInsnNode.desc);
                        methodVisitor.visitVarInsn(type.getOpcode(ILOAD), 1);
                        methodVisitor.visitFieldInsn(PUTFIELD, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
                        methodVisitor.visitInsn(RETURN);
                        methodVisitor.visitMaxs(type.getSize() + 1, type.getSize() + 1);
                        break;
                    }
                }
            } else {
                MethodInsnNode methodInsnNode = accessInfo.methodInsnNode;
                Type[] argTypes = Type.getArgumentTypes(methodInsnNode.desc);
                Type returnType = Type.getReturnType(methodInsnNode.desc);

                switch(methodInsnNode.getOpcode()) {
                    case INVOKEVIRTUAL:
                    case INVOKEINTERFACE:
                    case INVOKESPECIAL: {
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        int size = 1;
                        for (Type argType: argTypes) {
                            methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), size);
                            size += argType.getSize();
                        }
                        methodVisitor.visitMethodInsn(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf);
                        methodVisitor.visitInsn(returnType.getOpcode(IRETURN));
                        methodVisitor.visitMaxs(size, size);
                        break;
                    }

                    case INVOKESTATIC: {
                        int size = 0;
                        for (Type argType: argTypes) {
                            methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), size);
                            size += argType.getSize();
                        }
                        methodVisitor.visitMethodInsn(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf);
                        methodVisitor.visitInsn(returnType.getOpcode(IRETURN));
                        methodVisitor.visitMaxs(size, size);
                        break;
                    }
                }
            }
            methodVisitor.visitEnd();
        }

        for (ClassNameAndData classNameAndData: classNameAndDataList) {
            String name = classNameAndData.getClassName();
            cw.visitInnerClass(
                name,
                classNode.name,
                name.substring(name.lastIndexOf("$") + 1),
                ACC_PRIVATE | ACC_STATIC | ACC_FINAL
            );
        }
        byte[] bytes = cw.toByteArray();
        ClassNameAndData classNameAndData = new ClassNameAndData();
        classNameAndData.setClassName(classNode.name);
        classNameAndData.setData(bytes);
        classNameAndDataList.add(classNameAndData);
        return classNameAndDataList;
    }

    /**
     * 将同步方法转换为在新线程执行
     * @return
     */
    public List<GenerateClassData> transformNewRunnableCall(String suffix, MethodNode methodNode) throws AnalyzerException {
        List<GenerateClassData> ret = new ArrayList<>();

        ClassWriter taskClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String taskClassName = classNode.name + '$' + TASK_CLASS_NAME + suffix;
        taskClassWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, taskClassName, null, OBJECT_CLASS_NAME, new String[] { RUNNABLE_INTERFACE_NAME });
        taskClassWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + JAVA_FILE_SUFFIX, null);

        Map<Integer, List<LocalSetterInfo>> localSetterMap = copyLocalVariables(taskClassName, methodNode, taskClassWriter);
        Frame<BasicValue>[] frames = TransformerHelper.computeFrame(classNode.name, methodNode);
        String futurePropertyName = addFutureProperty(taskClassWriter);
        String[] completeMethodNameAndDesc = addCompleteMethod(taskClassWriter);
        addEmptyConstructor(taskClassWriter, taskClassName, futurePropertyName);

        int maxStackSize = 0;
        {
            MethodVisitor methodVisitor = taskClassWriter.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            MethodInsnTransformer methodInsnTransformer = new MethodInsnTransformer(
                methodVisitor,
                localSetterMap,
                frames,
                taskClassName,
                futurePropertyName
            );

            methodVisitor.visitCode();
            for (TryCatchBlockNode tryCatchBlockNode : methodNode.tryCatchBlocks) {
                methodVisitor.visitTryCatchBlock(
                    tryCatchBlockNode.start.getLabel(),
                    tryCatchBlockNode.end.getLabel(),
                    tryCatchBlockNode.handler.getLabel(),
                    tryCatchBlockNode.type
                );
            }
            Label startLabel = new Label();
            Label endLabel = new Label();
            Label handlerLabel = new Label();

            methodVisitor.visitTryCatchBlock(startLabel, endLabel, handlerLabel, THROWABLE_CLASS_NAME);
            methodVisitor.visitLabel(startLabel);
            for (int i = 0; i < methodNode.instructions.size(); i++) {
                AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                    if (TransformerHelper.isAsyncCall(methodInsnNode)) {
                        methodVisitor.visitInsn(NOP);
                    } else {
                        callMethod(methodInsnNode, methodVisitor, taskClassWriter);
                    }
                } else if (abstractInsnNode instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
                    getOrSetField(fieldInsnNode, methodVisitor, taskClassWriter);
                } else if (abstractInsnNode instanceof TableSwitchInsnNode) {
                    TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) abstractInsnNode;
                    Label[] labels = new Label[tableSwitchInsnNode.labels.size()];
                    for (int j = 0; j < tableSwitchInsnNode.labels.size(); j++) {
                        labels[j] = tableSwitchInsnNode.labels.get(j).getLabel();
                    }
                    methodVisitor.visitTableSwitchInsn(tableSwitchInsnNode.min, tableSwitchInsnNode.max, tableSwitchInsnNode.dflt.getLabel(), labels);
                } else if (abstractInsnNode instanceof LineNumberNode) {
                    LineNumberNode lineNumberNode = (LineNumberNode) abstractInsnNode;
                    methodVisitor.visitLineNumber(lineNumberNode.line, lineNumberNode.start.getLabel());
                } else if (abstractInsnNode instanceof IntInsnNode) {
                    IntInsnNode intInsnNode = (IntInsnNode) abstractInsnNode;
                    methodVisitor.visitIntInsn(intInsnNode.getOpcode(), intInsnNode.operand);
                } else if (abstractInsnNode instanceof IincInsnNode) {
                    methodInsnTransformer.transformIincInsnNode((IincInsnNode) abstractInsnNode, i);
                } else if (abstractInsnNode instanceof LabelNode) {
                    LabelNode labelNode = (LabelNode) abstractInsnNode;
                    methodVisitor.visitLabel(labelNode.getLabel());
                } else if (abstractInsnNode instanceof MultiANewArrayInsnNode) {
                    MultiANewArrayInsnNode multiANewArrayInsnNode = (MultiANewArrayInsnNode) abstractInsnNode;
                    methodVisitor.visitMultiANewArrayInsn(multiANewArrayInsnNode.desc, multiANewArrayInsnNode.dims);
                } else if (abstractInsnNode instanceof LdcInsnNode) {
                    LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                    methodVisitor.visitLdcInsn(ldcInsnNode.cst);
                } else if (abstractInsnNode instanceof TypeInsnNode) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) abstractInsnNode;
                    methodVisitor.visitTypeInsn(typeInsnNode.getOpcode(), typeInsnNode.desc);
                } else if (abstractInsnNode instanceof VarInsnNode) {
                    methodInsnTransformer.transformVarInsnNode((VarInsnNode) abstractInsnNode, i);
                } else if (abstractInsnNode instanceof FrameNode) {
                    // do nothing
                } else if (abstractInsnNode instanceof InvokeDynamicInsnNode) {
                    InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) abstractInsnNode;
                    methodVisitor.visitInvokeDynamicInsn(
                        invokeDynamicInsnNode.name,
                        invokeDynamicInsnNode.desc,
                        invokeDynamicInsnNode.bsm,
                        invokeDynamicInsnNode.bsmArgs
                    );
                } else if (abstractInsnNode instanceof JumpInsnNode) {
                    JumpInsnNode jumpInsnNode = (JumpInsnNode) abstractInsnNode;
                    methodVisitor.visitJumpInsn(jumpInsnNode.getOpcode(), jumpInsnNode.label.getLabel());
                } else if (abstractInsnNode instanceof LookupSwitchInsnNode) {
                    LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) abstractInsnNode;
                    int[] keys = new int[lookupSwitchInsnNode.keys.size()];
                    for (int j = 0; j < lookupSwitchInsnNode.keys.size(); j++) {
                        keys[j] = lookupSwitchInsnNode.keys.get(j);
                    }
                    Label[] labels = new Label[lookupSwitchInsnNode.labels.size()];
                    for (int j = 0; j < lookupSwitchInsnNode.labels.size(); j++) {
                        labels[j] = lookupSwitchInsnNode.labels.get(j).getLabel();
                    }
                    methodVisitor.visitLookupSwitchInsn(
                        lookupSwitchInsnNode.dflt.getLabel(),
                        keys,
                        labels
                    );
                } else if (abstractInsnNode instanceof InsnNode) {
                    int stackSize = methodInsnTransformer.transformReturn((InsnNode) abstractInsnNode, i, methodNode.instructions, completeMethodNameAndDesc);
                    maxStackSize = Math.max(stackSize, maxStackSize);
                }
            }
            // 通用错误处理代码
            methodVisitor.visitLabel(endLabel);
            methodInsnTransformer.exceptionHandler(handlerLabel);
            // 转换过程中未引入任何本地变量
            methodVisitor.visitMaxs(maxStackSize, 1);
            methodVisitor.visitEnd();
        }

        ret.add(new GenerateClassData(taskClassWriter, taskClassName, ClassType.TASK, localSetterMap));
        return ret;
    }

    /**
     * 将AWAIT方法转换为Task类
     * @param suffix class名称后缀
     * @param methodNode 方法节点
     * @return 转换结果
     * @throws AnalyzerException
     */
    public List<GenerateClassData> transformNewTaskCall(String suffix, MethodNode methodNode) throws AnalyzerException {
        List<GenerateClassData> ret = new ArrayList<>();

        ClassWriter taskClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String taskClassName = classNode.name + '$' + TASK_CLASS_NAME + suffix;
        taskClassWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, taskClassName, null, OBJECT_CLASS_NAME, new String[] { TASK_INTERFACE_NAME });
        taskClassWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + JAVA_FILE_SUFFIX, null);

        Frame<BasicValue>[] frames = TransformerHelper.computeFrame(classNode.name, methodNode);
        Map<Integer, List<LocalSetterInfo>> localSetterMap = copyLocalVariables(taskClassName, methodNode, taskClassWriter);
        String futurePropertyName = addFutureProperty(taskClassWriter);
        String handlerPropertyName = addHandlerProperty(taskClassWriter);
        String stackHolderPropertyName = addStackHolderProperty(taskClassWriter);
        String[] completeMethodNameAndDesc = addCompleteMethod(taskClassWriter);
        addEmptyConstructor(taskClassWriter, taskClassName, futurePropertyName);
        {
            int awaitCount = TransformerHelper.awaitInvokeCount(methodNode);
            MethodVisitor methodVisitor = taskClassWriter.visitMethod(ACC_PUBLIC, METHOD_NAME_MOVE_TO_NEXT, METHOD_DESC_MOVE_TO_NEXT, null, null);
            methodVisitor.visitCode();
            for (TryCatchBlockNode tryCatchBlockNode: methodNode.tryCatchBlocks) {
                methodVisitor.visitTryCatchBlock(
                    tryCatchBlockNode.start.getLabel(),
                    tryCatchBlockNode.end.getLabel(),
                    tryCatchBlockNode.handler.getLabel(),
                    tryCatchBlockNode.type
                );
            }

            Label startLabel = new Label();
            Label endLabel = new Label();
            Label handlerLabel = new Label();
            methodVisitor.visitTryCatchBlock(startLabel, endLabel, handlerLabel, THROWABLE_CLASS_NAME);
            methodVisitor.visitLabel(startLabel);

            SwitchInfo switchInfo = new SwitchInfo(awaitCount + 1);
            MethodInsnTransformer methodInsnTransformer = new MethodInsnTransformer(
                methodVisitor,
                localSetterMap,
                frames,
                taskClassName,
                futurePropertyName
            );
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitTableSwitchInsn(0, awaitCount, switchInfo.defaultLabel, switchInfo.getSwitchLabelList());

            int maxStackSize = 0;
            int labelIndex = 0;
            methodVisitor.visitLabel(switchInfo.getResumeLabel(labelIndex));
            for (int i = 0; i < methodNode.instructions.size(); i++) {
                AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                // 复制字节码，转换本地变量表和await调用
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                    if (TransformerHelper.isAwaitCall(methodInsnNode)) {
                        labelIndex++;
                        Frame<BasicValue> currentFrame = frames[i];
                        switchInfo.setFrame(labelIndex, currentFrame);
                        methodInsnTransformer.createAndPutHandler(labelIndex, handlerPropertyName);
                        if (currentFrame != null) {
                            int newMax = Math.max(currentFrame.getStackSize() + 5, currentFrame.getMaxStackSize());
                            maxStackSize = Math.max(maxStackSize, newMax);
                        }
                        if (currentFrame != null && currentFrame.getStackSize() > 1) {
                            String stackClassName = classNode.name +  "$StackHolder" + suffix + labelIndex;
                            // saveAndRestoreStack方法处理了return和visitLabel
                            ret.add(switchInfo.saveAndRestoreStack(
                                labelIndex,
                                classNode,
                                methodVisitor,
                                stackClassName,
                                taskClassName,
                                stackHolderPropertyName
                            ));
                        } else {
                            methodVisitor.visitInsn(RETURN);
                            methodVisitor.visitLabel(switchInfo.getResumeLabel(labelIndex));
                        }
                        methodInsnTransformer.onAwaitReturn(handlerPropertyName);
                    } else if (TransformerHelper.isAsyncCall(methodInsnNode)) {
                        methodVisitor.visitInsn(NOP);
                    } else {
                        callMethod(methodInsnNode, methodVisitor, taskClassWriter);
                    }
                } else if (abstractInsnNode instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
                    getOrSetField(fieldInsnNode, methodVisitor, taskClassWriter);
                } else if (abstractInsnNode instanceof TableSwitchInsnNode) {
                    TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) abstractInsnNode;
                    Label[] labels = new Label[tableSwitchInsnNode.labels.size()];
                    for (int j = 0; j < tableSwitchInsnNode.labels.size(); j++) {
                        labels[j] = tableSwitchInsnNode.labels.get(j).getLabel();
                    }
                    methodVisitor.visitTableSwitchInsn(tableSwitchInsnNode.min, tableSwitchInsnNode.max, tableSwitchInsnNode.dflt.getLabel(), labels);
                } else if (abstractInsnNode instanceof LineNumberNode) {
                    LineNumberNode lineNumberNode = (LineNumberNode) abstractInsnNode;
                    methodVisitor.visitLineNumber(lineNumberNode.line, lineNumberNode.start.getLabel());
                } else if (abstractInsnNode instanceof IntInsnNode) {
                    IntInsnNode intInsnNode = (IntInsnNode) abstractInsnNode;
                    methodVisitor.visitIntInsn(intInsnNode.getOpcode(), intInsnNode.operand);
                } else if (abstractInsnNode instanceof IincInsnNode) {
                    methodInsnTransformer.transformIincInsnNode((IincInsnNode) abstractInsnNode, i);
                } else if (abstractInsnNode instanceof LabelNode) {
                    LabelNode labelNode = (LabelNode) abstractInsnNode;
                    methodVisitor.visitLabel(labelNode.getLabel());
                } else if (abstractInsnNode instanceof MultiANewArrayInsnNode) {
                    MultiANewArrayInsnNode multiANewArrayInsnNode = (MultiANewArrayInsnNode) abstractInsnNode;
                    methodVisitor.visitMultiANewArrayInsn(multiANewArrayInsnNode.desc, multiANewArrayInsnNode.dims);
                } else if (abstractInsnNode instanceof LdcInsnNode) {
                    LdcInsnNode ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                    methodVisitor.visitLdcInsn(ldcInsnNode.cst);
                } else if (abstractInsnNode instanceof TypeInsnNode) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) abstractInsnNode;
                    methodVisitor.visitTypeInsn(typeInsnNode.getOpcode(), typeInsnNode.desc);
                } else if (abstractInsnNode instanceof VarInsnNode) {
                    methodInsnTransformer.transformVarInsnNode((VarInsnNode) abstractInsnNode, i);
                } else if (abstractInsnNode instanceof FrameNode) {
                    // do nothing
                } else if (abstractInsnNode instanceof InvokeDynamicInsnNode) {
                    InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) abstractInsnNode;
                    methodVisitor.visitInvokeDynamicInsn(
                        invokeDynamicInsnNode.name,
                        invokeDynamicInsnNode.desc,
                        invokeDynamicInsnNode.bsm,
                        invokeDynamicInsnNode.bsmArgs
                    );
                } else if (abstractInsnNode instanceof JumpInsnNode) {
                    JumpInsnNode jumpInsnNode = (JumpInsnNode) abstractInsnNode;
                    methodVisitor.visitJumpInsn(jumpInsnNode.getOpcode(), jumpInsnNode.label.getLabel());
                } else if (abstractInsnNode instanceof LookupSwitchInsnNode) {
                    LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) abstractInsnNode;
                    int[] keys = new int[lookupSwitchInsnNode.keys.size()];
                    for (int j = 0; j < lookupSwitchInsnNode.keys.size(); j++) {
                        keys[j] = lookupSwitchInsnNode.keys.get(j);
                    }
                    Label[] labels = new Label[lookupSwitchInsnNode.labels.size()];
                    for (int j = 0; j < lookupSwitchInsnNode.labels.size(); j++) {
                        labels[j] = lookupSwitchInsnNode.labels.get(j).getLabel();
                    }
                    methodVisitor.visitLookupSwitchInsn(
                        lookupSwitchInsnNode.dflt.getLabel(),
                        keys,
                        labels
                    );
                } else if (abstractInsnNode instanceof InsnNode) {
                    int stackSize = methodInsnTransformer.transformReturn((InsnNode) abstractInsnNode, i, methodNode.instructions, completeMethodNameAndDesc);
                    maxStackSize = Math.max(stackSize, maxStackSize);
                }
            }
            // default label，抛出异常
            switchInfo.throwExceptionDefaultLabel(methodVisitor);
            methodVisitor.visitLabel(endLabel);
            // 通用错误处理代码
            methodInsnTransformer.exceptionHandler(handlerLabel);
            // 转换过程中未引入任何本地变量
            methodVisitor.visitMaxs(maxStackSize, 2);
            methodVisitor.visitEnd();
        }
        taskClassWriter.visitEnd();
        ret.add(new GenerateClassData(taskClassWriter, taskClassName, ClassType.TASK, localSetterMap));
        return ret;
    }

    public String getPath() {
        return path;
    }

    public static String addFutureProperty(ClassWriter classWriter) {
        String name = "_future";
        FieldVisitor fieldVisitor = classWriter.visitField(
            ACC_PUBLIC,
            name,
            FUTURE_FIELD_DESC,
            null,  // 泛型签名,
            null
        );
        fieldVisitor.visitEnd();
        return name;
    }

    public static String addHandlerProperty(ClassWriter classWriter) {
        String name = "_handler";
        FieldVisitor fieldVisitor = classWriter.visitField(
            ACC_PUBLIC,
            name,
            HANDLER_FIELD_DESC,
            null,  // 泛型签名,
            null
        );
        fieldVisitor.visitEnd();

        return name;
    }

    public static String addStackHolderProperty(ClassWriter classWriter) {
        String name = "_stackHolder";
        FieldVisitor fieldVisitor = classWriter.visitField(
            ACC_PUBLIC,
            name,
            OBJECT_CLASS_DESC,
            null,  // 泛型签名,
            null
        );
        fieldVisitor.visitEnd();
        return name;
    }


    /**
     * 将本地变量表复制到Task类，并生成setter方法
     * @param className 方法所在的类
     * @param methodNode 方法
     * @param classWriter classWriter
     * @return 本地变量index => name、desc、setter、setter desc
     */
    public static Map<Integer, List<LocalSetterInfo>> copyLocalVariables(String className, MethodNode methodNode, ClassWriter classWriter) {
        Map<Integer, List<LocalSetterInfo>> propertyNames = new TreeMap<>();
        String classDesc = "L" + className + ";";

        List<LocalVariableNode> tmp = new ArrayList<>(methodNode.localVariables.size());
        tmp.addAll(methodNode.localVariables);
        tmp.sort(Comparator.comparingInt(n -> n.index));

        for (int i = 0; i < tmp.size(); i++) {
            LocalVariableNode localVariable = tmp.get(i);
            String name = "local_" + i;
            String desc = localVariable.desc;

            FieldVisitor fieldVisitor = classWriter.visitField(
                ACC_PUBLIC,
                name,
                desc,
                null,  // localVariable.signature,
                null
            );
            fieldVisitor.visitEnd();

            int startIndex = methodNode.instructions.indexOf(localVariable.start);
            int endIndex = methodNode.instructions.indexOf(localVariable.end);

            String setterName = SETTER_PREFIX + name;
            String setterDesc = "(" + desc + classDesc + ")V";
            LocalSetterInfo setterInfo = new LocalSetterInfo(name, desc, setterName, setterDesc);
            setterInfo.setStartIndex(startIndex);
            setterInfo.setEndIndex(endIndex);

            List<LocalSetterInfo> list = propertyNames.computeIfAbsent(localVariable.index, k -> new ArrayList<>());
            list.add(setterInfo);
            MethodVisitor methodVisitor = classWriter.visitMethod(
                ACC_PRIVATE | ACC_STATIC,
                setterName,
                setterDesc,
                null,
                null
            );

            Type type = Type.getType(desc);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, type.getSize());
            methodVisitor.visitVarInsn(type.getOpcode(ILOAD), 0);
            methodVisitor.visitFieldInsn(PUTFIELD, className, name, desc);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(type.getSize() + 1, type.getSize() + 1);
            methodVisitor.visitEnd();
        }
        return propertyNames;
    }

    public static String[] addCompleteMethod(ClassWriter classWriter) {
        String methodName = "completeFuture";
        String methodDesc = "(Ljava/lang/Object;Lcom/whatswater/async/future/TaskFutureImpl;)V";
        MethodVisitor methodVisitor = classWriter.visitMethod(
            ACC_PRIVATE | ACC_STATIC,
            methodName,
            methodDesc,
            null,
            null
        );
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            FUTURE_CLASS_NAME,
            FUTURE_CLASS_TRY_COMPLETE_METHOD_NAME,
            FUTURE_CLASS_TRY_COMPLETE_METHOD_DESC,
            false
        );
        methodVisitor.visitInsn(POP);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        return new String[] { methodName, methodDesc };
    }

    public static void addEmptyConstructor(ClassWriter classWriter, String className, String futurePropertyName) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, OBJECT_CLASS_NAME, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitTypeInsn(NEW, FUTURE_CLASS_NAME);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, FUTURE_CLASS_NAME, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
        methodVisitor.visitFieldInsn(PUTFIELD, className, futurePropertyName, FUTURE_FIELD_DESC);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(3, 1);
        methodVisitor.visitEnd();
    }


    public static Map<Integer, String[]> generateStackMapHolder(ClassNode classNode, ClassWriter classWriter, String className, Frame<BasicValue> currentFrame) {
        Map<Integer, String[]> propertyNameList = new TreeMap<>();

        String classDesc = "L" + className + ";";
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, OBJECT_CLASS_NAME, null);
        classWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + JAVA_FILE_SUFFIX, null);
        {
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, OBJECT_CLASS_NAME, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", classDesc, null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }

        for (int i = 0; i < currentFrame.getStackSize() - 1; i++) {
            BasicValue basicValue = currentFrame.getStack(i);
            if (basicValue.getType() == null) {
                continue;
            }

            String fieldName = "stack_" + i;
            String fieldDesc = basicValue.getType().getDescriptor();
            FieldVisitor fieldVisitor = classWriter.visitField(
                ACC_PUBLIC,
                fieldName,
                fieldDesc,
                null,
                null
            );
            fieldVisitor.visitEnd();

            String setterName = SETTER_PREFIX + fieldName;
            String setterDesc = "(" + fieldDesc + classDesc + ")V";
            MethodVisitor methodVisitor = classWriter.visitMethod(
                ACC_PUBLIC | ACC_STATIC,
                setterName,
                setterDesc,
                null,
                null
            );
            Type type = Type.getType(fieldDesc);

            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, type.getSize());
            methodVisitor.visitVarInsn(type.getOpcode(ILOAD), 0);
            methodVisitor.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(type.getSize() + 1, type.getSize() + 1);
            methodVisitor.visitEnd();

            propertyNameList.put(i, new String[] { fieldName, fieldDesc, setterName, setterDesc });
        }
        classWriter.visitEnd();
        return propertyNameList;
    }

    public static MethodNode generateCallTaskMethod(ClassNode classNode, MethodNode methodNode, List<GenerateClassData> classDataList) {
        final MethodNode replacement = new MethodNode(
            methodNode.access,
            methodNode.name,
            methodNode.desc,
            methodNode.signature,
            methodNode.exceptions.toArray(new String[0])
        );

        GenerateClassData taskClassData = TransformerHelper.findTaskClass(classDataList);
        if (taskClassData == null) {
            return null;
        }
        String taskClassName = taskClassData.getClassName();

        Map<Integer, List<LocalSetterInfo>> propertyNames = taskClassData.getNames();
        Type type = Type.getMethodType(methodNode.desc);
        Type[] argTypes = type.getArgumentTypes();

        replacement.visitCode();
        final int argOffset = Modifier.isStatic(methodNode.access) ? 0 : 1;
        int idx = argOffset;
        for (Type value : argTypes) {
            idx += value.getSize();
        }

        // 创建Task
        replacement.visitTypeInsn(NEW, taskClassName);
        replacement.visitInsn(DUP);
        replacement.visitMethodInsn(INVOKESPECIAL, taskClassName, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
        replacement.visitVarInsn(ASTORE, idx);

        // 将this赋值到Task
        if (argOffset > 0) {
            LocalSetterInfo localSetterInfo = TransformerHelper.findFirstSetterMethod(propertyNames, 0);
            if (localSetterInfo == null) {
                throw new RuntimeException("can't find local variable 'this', type: " + classNode.name + ", method: " + methodNode.desc);
            }

            String propertyName = localSetterInfo.getName();
            String propertyDesc = localSetterInfo.getDesc();
            replacement.visitVarInsn(ALOAD, idx);
            replacement.visitVarInsn(ALOAD, 0);
            replacement.visitFieldInsn(PUTFIELD, taskClassName, propertyName, propertyDesc);
        }
        // 将参数赋值给Task
        int index = 0;
        for (int i = 0; i < argTypes.length; i++) {
            Type argType = type.getArgumentTypes()[i];

            LocalSetterInfo localSetterInfo = TransformerHelper.findFirstSetterMethod(propertyNames, i + argOffset);
            if (localSetterInfo == null) {
                throw new RuntimeException("can't find local variable, argType: " + argType.getDescriptor() + " type: " + classNode.name + ", method: " + methodNode.desc);
            }
            String propertyName = localSetterInfo.getName();
            String propertyDesc = localSetterInfo.getDesc();

            replacement.visitVarInsn(ALOAD, idx);
            replacement.visitVarInsn(argType.getOpcode(ILOAD), index + argOffset);
            replacement.visitFieldInsn(PUTFIELD, taskClassName, propertyName, propertyDesc);
            index += argType.getSize();
        }
        // 调用moveToNext
        replacement.visitVarInsn(ALOAD, idx);
        replacement.visitInsn(ICONST_0);
        replacement.visitMethodInsn(INVOKEVIRTUAL, taskClassName, METHOD_NAME_MOVE_TO_NEXT, METHOD_DESC_MOVE_TO_NEXT, false);
        // 获取_future属性
        replacement.visitVarInsn(ALOAD, idx);
        replacement.visitFieldInsn(GETFIELD, taskClassName, "_future", FUTURE_FIELD_DESC);

        String returnClassName = type.getReturnType().getInternalName();
        if (!FUTURE_CLASS_NAME.equals(returnClassName)) {
            replacement.visitTypeInsn(CHECKCAST, returnClassName);
        }
        replacement.visitInsn(ARETURN);

        // idx > argOffset + argTypes.length时，存在long类型数据；idx + 1，本地变量+this
        replacement.visitMaxs(idx > argOffset + argTypes.length ? 3 : 2, idx + 1);
        replacement.visitEnd();
        return replacement;
    }

    public static MethodNode generateCallRunnableMethod(ClassNode classNode, MethodNode methodNode, List<GenerateClassData> classDataList) {
        final MethodNode replacement = new MethodNode(
            methodNode.access,
            methodNode.name,
            methodNode.desc,
            methodNode.signature,
            methodNode.exceptions.toArray(new String[0])
        );

        GenerateClassData taskClassData = TransformerHelper.findTaskClass(classDataList);
        if (taskClassData == null) {
            return null;
        }

        String taskClassName = taskClassData.getClassName();
        Map<Integer, List<LocalSetterInfo>> propertyNames = taskClassData.getNames();
        Type type = Type.getMethodType(methodNode.desc);
        Type[] argTypes = type.getArgumentTypes();

        replacement.visitCode();
        final int argOffset = Modifier.isStatic(methodNode.access) ? 0 : 1;
        int idx = argOffset;
        for (Type value : argTypes) {
            idx += value.getSize();
        }

        // 创建Task
        replacement.visitTypeInsn(NEW, taskClassName);
        replacement.visitInsn(DUP);
        replacement.visitMethodInsn(INVOKESPECIAL, taskClassName, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
        replacement.visitVarInsn(ASTORE, idx);

        // 将this赋值到Task
        if (argOffset > 0) {
            LocalSetterInfo localSetterInfo = TransformerHelper.findFirstSetterMethod(propertyNames, 0);
            if (localSetterInfo == null) {
                throw new RuntimeException("can't find local variable 'this', type: " + classNode.name + ", method: " + methodNode.desc);
            }

            String propertyName = localSetterInfo.getName();
            String propertyDesc = localSetterInfo.getDesc();
            replacement.visitVarInsn(ALOAD, idx);
            replacement.visitVarInsn(ALOAD, 0);
            replacement.visitFieldInsn(PUTFIELD, taskClassName, propertyName, propertyDesc);
        }
        // 将参数赋值给Task
        int index = 0;
        for (int i = 0; i < argTypes.length; i++) {
            Type argType = type.getArgumentTypes()[i];

            LocalSetterInfo localSetterInfo = TransformerHelper.findFirstSetterMethod(propertyNames, i + argOffset);
            if (localSetterInfo == null) {
                throw new RuntimeException("can't find local variable, argType: " + argType.getDescriptor() + " type: " + classNode.name + ", method: " + methodNode.desc);
            }
            String propertyName = localSetterInfo.getName();
            String propertyDesc = localSetterInfo.getDesc();

            replacement.visitVarInsn(ALOAD, idx);
            replacement.visitVarInsn(argType.getOpcode(ILOAD), index + argOffset);
            replacement.visitFieldInsn(PUTFIELD, taskClassName, propertyName, propertyDesc);
            index += argType.getSize();
        }
        replacement.visitFieldInsn(GETSTATIC, "com/whatswater/async/type/Async", "executor", "Ljava/util/concurrent/ExecutorService;");
        replacement.visitVarInsn(ALOAD, idx);
        replacement.visitMethodInsn(INVOKEINTERFACE, "java/util/concurrent/ExecutorService", "execute", "(Ljava/lang/Runnable;)V", true);

        // 获取_future属性
        replacement.visitVarInsn(ALOAD, idx);
        replacement.visitFieldInsn(GETFIELD, taskClassName, "_future", FUTURE_FIELD_DESC);

        String returnClassName = type.getReturnType().getInternalName();
        if (!FUTURE_CLASS_NAME.equals(returnClassName)) {
            replacement.visitTypeInsn(CHECKCAST, returnClassName);
        }
        replacement.visitInsn(ARETURN);

        // idx > argOffset + argTypes.length时，存在long类型数据；idx + 1，本地变量+this
        replacement.visitMaxs(idx > argOffset + argTypes.length ? 3 : 2, idx + 1);
        replacement.visitEnd();
        return replacement;
    }

    private void callMethod(MethodInsnNode methodInsnNode, MethodVisitor methodVisitor, ClassWriter taskClassWriter) throws AnalyzerException {
        if (methodInsnNode.owner.equals(classNode.name)) {
            MethodNode callMethodNode = TransformerHelper.findMethodNode(methodInsnNode.name, methodInsnNode.desc, classNode);
            if (callMethodNode == null) {
                throw new AnalyzerException(methodInsnNode, "method: " + methodInsnNode.name + "desc: " + methodInsnNode.desc + " not exist in class: " + methodInsnNode.owner);
            }

            if (Modifier.isPrivate(callMethodNode.access)) {
                AccessInfo accessInfo = getAllCreateAccessMethod(methodInsnNode);
                if (accessInfo.desc == null || accessInfo.desc.length() == 0) {
                    switch (methodInsnNode.getOpcode()) {
                        case INVOKESTATIC:
                            accessInfo.desc = methodInsnNode.desc;
                            break;
                        case INVOKEVIRTUAL:
                        case INVOKEINTERFACE:
                        case INVOKESPECIAL:
                            Type[] argTypes = Type.getArgumentTypes(methodInsnNode.desc);
                            Type returnType = Type.getReturnType(methodInsnNode.desc);

                            StringBuilder tmp = new StringBuilder();
                            tmp.append("L").append(methodInsnNode.owner).append(";");
                            for (Type argType : argTypes) {
                                tmp.append(argType.getDescriptor());
                            }
                            tmp.append(")").append(returnType.getDescriptor());
                            accessInfo.desc = tmp.toString();
                            break;
                    }
                }
                methodVisitor.visitMethodInsn(INVOKESTATIC, methodInsnNode.owner, accessInfo.methodName, accessInfo.desc, false);
            }
            else {
                methodVisitor.visitMethodInsn(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf);
            }
        } else {
            for (InnerClassNode innerClass : classNode.innerClasses) {
                if (innerClass.name.equals(methodInsnNode.owner)) {
                    taskClassWriter.visitInnerClass(innerClass.name, innerClass.outerName, innerClass.innerName, innerClass.access);
                    break;
                }
            }
            methodVisitor.visitMethodInsn(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf);
        }
    }

    private void getOrSetField(FieldInsnNode fieldInsnNode, MethodVisitor methodVisitor, ClassWriter taskClassWriter) throws AnalyzerException {
        if (fieldInsnNode.owner.equals(classNode.name)) {
            FieldNode fieldNode = TransformerHelper.findFieldNode(fieldInsnNode.name, classNode);
            if (fieldNode == null) {
                throw new AnalyzerException(fieldInsnNode, "field: " + fieldInsnNode.name + " not exist in class: " + fieldInsnNode.owner);
            }
            if (Modifier.isPrivate(fieldNode.access)) {
                AccessInfo accessInfo = getAllCreateAccessMethod(fieldInsnNode);
                if (accessInfo.desc == null || accessInfo.desc.length() == 0) {
                    switch (fieldInsnNode.getOpcode()) {
                        case GETFIELD:
                            accessInfo.desc = "(L" + fieldInsnNode.owner + ";)" + fieldInsnNode.desc;
                            break;
                        case PUTFIELD:
                            accessInfo.desc = "(L" + fieldInsnNode.owner + ";" + fieldInsnNode.desc + ")V";
                            break;
                        case GETSTATIC:
                            accessInfo.desc = "()" + fieldInsnNode.desc;
                            break;
                        case PUTSTATIC:
                            accessInfo.desc = "(" + fieldInsnNode.desc + ")V";
                            break;
                    }
                }
                methodVisitor.visitMethodInsn(INVOKESTATIC, fieldInsnNode.owner, accessInfo.methodName, accessInfo.desc, false);
            } else {
                methodVisitor.visitFieldInsn(fieldInsnNode.getOpcode(), fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
            }
        } else {
            for (InnerClassNode innerClass : classNode.innerClasses) {
                if (innerClass.name.equals(fieldInsnNode.owner)) {
                    taskClassWriter.visitInnerClass(innerClass.name, innerClass.outerName, innerClass.innerName, innerClass.access);
                    break;
                }
            }
            methodVisitor.visitFieldInsn(fieldInsnNode.getOpcode(), fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
        }
    }

    private AccessInfo getAllCreateAccessMethod(FieldInsnNode fieldNode) {
        for (AccessInfo accessInfo: accessInfoList) {
            if (accessInfo.fieldInsnNode != null) {
                if (accessInfo.fieldInsnNode.name.equals(fieldNode.name)) {
                    return accessInfo;
                }
            }
        }

        accessCount = accessCount + 1;
        String methodName = ACCESS_PREFIX + accessCount;

        AccessInfo accessInfo = new AccessInfo();
        accessInfo.fieldInsnNode = fieldNode;
        accessInfo.methodName = methodName;
        this.accessInfoList.add(accessInfo);

        return accessInfo;
    }

    private AccessInfo getAllCreateAccessMethod(MethodInsnNode methodNode) {
        for (AccessInfo accessInfo: accessInfoList) {
            if (accessInfo.methodInsnNode != null) {
                if (accessInfo.methodInsnNode.name.equals(methodNode.name) && accessInfo.methodInsnNode.desc.equals(methodNode.desc)) {
                    return accessInfo;
                }
            }
        }

        accessCount = accessCount + 1;
        String methodName = ACCESS_PREFIX + accessCount;

        AccessInfo accessInfo = new AccessInfo();
        accessInfo.methodInsnNode = methodNode;
        accessInfo.methodName = methodName;

        this.accessInfoList.add(accessInfo);
        return accessInfo;
    }

    private static class AccessInfo {
        String methodName;
        FieldInsnNode fieldInsnNode;
        MethodInsnNode methodInsnNode;
        String desc;
    }
}
