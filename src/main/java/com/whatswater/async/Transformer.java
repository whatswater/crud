package com.whatswater.async;


import com.whatswater.async.GenerateClassData.ClassType;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

// TODO
// 1、处理内部类权限问题
// 2、优化代码
// 3、开发maven插件
public class Transformer {
    public static final String TASK_CLASS_NAME = "Task";
    public static final String JAVA_FILE_SUFFIX = ".java";
    public static final String TASK_EXTEND_CLASS = "java/lang/Object";
    public static final String TASK_INTERFACE_NAME = "com/whatswater/async/Task";
    public static final String METHOD_NAME_MOVE_TO_NEXT = "moveToNext";
    public static final String METHOD_DESC_MOVE_TO_NEXT = "(I)V";

    public static final String HANDLER_FIELD_DESC = "Lcom/whatswater/async/handler/AwaitTaskHandler;";
    public static final String HANDLER_CLASS_NAME = "com/whatswater/async/handler/AwaitTaskHandler";
    public static final String FUTURE_INTERFACE_NAME = "io/vertx/core/Future";
    public static final String OBJECT_CLASS_DESC = "Ljava/lang/Object;";

    private final String path;
    private final ClassReader reader;
    private final ClassNode classNode;

    public Transformer(String path) throws IOException {
        this.path = path;
        this.reader = new ClassReader(path);
        this.classNode = this.readClassNode();
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
//                TaskClassGenerator taskClassGenerator = new TaskClassGenerator(classNode, methodNode, suffix);
//                taskClassGenerator.generate();
//                List<GenerateClassData> classDataList = taskClassGenerator.getGenerateClassDataList();

                List<GenerateClassData> classDataList = generateClass(suffix, methodNode);
                MethodNode replacement = TransformerHelper.generateCallTaskMethod(classNode, methodNode, classDataList);
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
     * 将AWAIT方法转换为Task类
     * @param suffix class名称后缀
     * @param methodNode 方法节点
     * @return 转换结果
     * @throws AnalyzerException
     */
    public List<GenerateClassData> generateClass(String suffix, MethodNode methodNode) throws AnalyzerException {
        List<GenerateClassData> ret = new ArrayList<>();

        ClassWriter taskClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String taskClassName = classNode.name + '$' + TASK_CLASS_NAME + suffix;
        taskClassWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, taskClassName, null, TASK_EXTEND_CLASS, new String[] { TASK_INTERFACE_NAME });
        taskClassWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + JAVA_FILE_SUFFIX, null);

        Frame<BasicValue>[] frames = TransformerHelper.computeFrame(classNode.name, methodNode);
        Map<Integer, List<LocalSetterInfo>> propertyNameAndDescList = TransformerHelper.copyLocalVariablesToProperties(taskClassName, methodNode, taskClassWriter);
        String futurePropertyName = TransformerHelper.addFutureProperty(taskClassWriter);
        String handlerPropertyName = TransformerHelper.addHandlerProperty(taskClassWriter);
        String stackHolderPropertyName = TransformerHelper.addStackHolderProperty(taskClassWriter);
        String[] completeMethodNameAndDesc = TransformerHelper.addCompleteMethod(taskClassWriter);
        TransformerHelper.addEmptyConstructor(taskClassWriter, taskClassName, futurePropertyName);
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
            methodVisitor.visitTryCatchBlock(startLabel, endLabel, handlerLabel, "java/lang/Throwable");

            methodVisitor.visitLabel(startLabel);
            Label defaultLabel = new Label();
            Label[] switchLabels = new Label[awaitCount + 1];
            for (int i = 0; i < awaitCount + 1; i++) {
                switchLabels[i] = new Label();
            }
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitTableSwitchInsn(0, awaitCount, defaultLabel, switchLabels);

            int maxStackSize = 0;
            int labelIndex = 0;
            methodVisitor.visitLabel(switchLabels[labelIndex]);
            for (int i = 0; i < methodNode.instructions.size(); i++) {
                AbstractInsnNode abstractInsnNode = methodNode.instructions.get(i);
                // 复制字节码，转换本地变量表和await调用
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                    if (TransformerHelper.isAwaitCall(methodInsnNode)) {
                        Frame<BasicValue> currentFrame = frames[i];
                        if (currentFrame != null) {
                            int newMax = Math.max(currentFrame.getStackSize() + 5, currentFrame.getMaxStackSize());
                            maxStackSize = Math.max(maxStackSize, newMax);
                        }

                        // 原先的栈顶，为一个Future对象
                        // 创建AwaitTaskHandler对象，并传入this和labelIndex，将新创建的AwaitTaskHandler设置为handlerPropertyName属性
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitTypeInsn(NEW, "com/whatswater/async/handler/AwaitTaskHandler");
                        methodVisitor.visitInsn(DUP);
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        labelIndex = labelIndex + 1;
                        switch (labelIndex) {
                            case 1:
                                methodVisitor.visitInsn(ICONST_1);
                                break;
                            case 2:
                                methodVisitor.visitInsn(ICONST_2);
                                break;
                            case 3:
                                methodVisitor.visitInsn(ICONST_3);
                                break;
                            case 4:
                                methodVisitor.visitInsn(ICONST_4);
                                break;
                            case 5:
                                methodVisitor.visitInsn(ICONST_5);
                                break;
                            default:
                                if (labelIndex <= Byte.MAX_VALUE) {
                                    methodVisitor.visitIntInsn(BIPUSH, labelIndex);
                                } else if (labelIndex <= Short.MAX_VALUE) {
                                    methodVisitor.visitIntInsn(SIPUSH, labelIndex);
                                } else {
                                    methodVisitor.visitLdcInsn(labelIndex);
                                }
                        }
                        methodVisitor.visitMethodInsn(INVOKESPECIAL, HANDLER_CLASS_NAME, "<init>", "(Lcom/whatswater/async/Task;I)V", false);
                        methodVisitor.visitFieldInsn(PUTFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);

                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD,  taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
                        // 栈顶的对象在此使用，调用onComplete后，栈顶存在一个新的Future对象
                        methodVisitor.visitMethodInsn(INVOKEINTERFACE, FUTURE_INTERFACE_NAME, "onComplete", "(Lio/vertx/core/Handler;)Lio/vertx/core/Future;", true);
                        // 丢失掉新的Future对象
                        methodVisitor.visitInsn(POP);

                        if (currentFrame != null && currentFrame.getStackSize() > 1) {
                            String stackClassName = classNode.name +  "$StackHolder" + suffix + labelIndex;
                            ClassWriter stackClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                            Map<Integer, String[]> stackHolderNameList = TransformerHelper.generateStackMapHolder(classNode, stackClassWriter, stackClassName, currentFrame);
                            ret.add(new GenerateClassData(stackClassWriter, stackClassName, ClassType.STACK_HOLDER, null));

                            methodVisitor.visitVarInsn(ALOAD, 0);
                            methodVisitor.visitTypeInsn(NEW, stackClassName);
                            methodVisitor.visitInsn(DUP);
                            methodVisitor.visitMethodInsn(INVOKESPECIAL, stackClassName, "<init>", "()V", false);
                            methodVisitor.visitFieldInsn(PUTFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);

                            // 保存栈数据
                            for (int index = currentFrame.getStackSize() - 2; index >= 0; index--) {
                                String[] names = stackHolderNameList.get(index);
                                if (names == null) {
                                    continue;
                                }

                                String setterName = names[2];
                                String setterDesc = names[3];
                                methodVisitor.visitVarInsn(ALOAD, 0);
                                methodVisitor.visitFieldInsn(GETFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);
                                methodVisitor.visitTypeInsn(CHECKCAST, stackClassName);
                                methodVisitor.visitMethodInsn(INVOKESTATIC, stackClassName, setterName, setterDesc, false);
                            }
                            methodVisitor.visitInsn(RETURN);
                            methodVisitor.visitLabel(switchLabels[labelIndex]);

                            // 恢复栈数据
                            for (int index = 0; index < currentFrame.getStackSize() - 1; index++) {
                                String[] names = stackHolderNameList.get(index);
                                if (names == null) {
                                    continue;
                                }

                                String propertyName = names[0];
                                String propertyDesc = names[1];
                                methodVisitor.visitVarInsn(ALOAD, 0);
                                methodVisitor.visitFieldInsn(GETFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);
                                methodVisitor.visitTypeInsn(CHECKCAST, stackClassName);
                                methodVisitor.visitFieldInsn(GETFIELD, stackClassName, propertyName, propertyDesc);
                            }
                        } else {
                            methodVisitor.visitInsn(RETURN);
                            methodVisitor.visitLabel(switchLabels[labelIndex]);
                        }

                        // 判断执行结果是否成功
                        Label toLabel = new Label();
                        Label elseLabel = new Label();
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, "Lcom/whatswater/async/handler/AwaitTaskHandler;");
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/whatswater/async/handler/AwaitTaskHandler", "succeeded", "()Z", false);
                        methodVisitor.visitJumpInsn(IFEQ, elseLabel);

                        // 成功后执行
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, "Lcom/whatswater/async/handler/AwaitTaskHandler;");
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/whatswater/async/handler/AwaitTaskHandler", "getResult", "()Ljava/lang/Object;", false);
                        methodVisitor.visitJumpInsn(GOTO, toLabel);

                        // 失败后执行
                        methodVisitor.visitLabel(elseLabel);
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, "Lcom/whatswater/async/handler/AwaitTaskHandler;");
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/whatswater/async/handler/AwaitTaskHandler", "getThrowable", "()Ljava/lang/Throwable;", false);
                        methodVisitor.visitInsn(ATHROW);

                        methodVisitor.visitLabel(toLabel);
                    } else if (TransformerHelper.isAsyncCall(methodInsnNode)) {
                        methodVisitor.visitInsn(NOP);
                    } else {
                        methodVisitor.visitMethodInsn(methodInsnNode.getOpcode(), methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, methodInsnNode.itf);
                    }
                } else if (abstractInsnNode instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;
                    methodVisitor.visitFieldInsn(fieldInsnNode.getOpcode(), fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc);
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
                    IincInsnNode iincInsnNode = (IincInsnNode) abstractInsnNode;
                    int index = iincInsnNode.var;
                    List<LocalSetterInfo> localSetterInfoList = propertyNameAndDescList.get(index);
                    LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterInfoList, Type.INT_TYPE.getDescriptor());
                    if (localSetterInfo == null) {
                        throw new RuntimeException("iinc var not right");
                    }
                    String propertyName = localSetterInfo.getName();
                    String propertyDesc = localSetterInfo.getDesc();
                    String setterName = localSetterInfo.getSetterName();
                    String setterDesc = localSetterInfo.getSetterDesc();

                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitFieldInsn(GETFIELD, taskClassName, propertyName, propertyDesc);
                    methodVisitor.visitVarInsn(ILOAD, iincInsnNode.incr);
                    methodVisitor.visitInsn(IADD);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, taskClassName, setterName, setterDesc, false);
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
                    Frame<BasicValue> currentFrame = frames[i];
                    VarInsnNode varInsnNode = (VarInsnNode) abstractInsnNode;
                    int index = varInsnNode.var;
                    List<LocalSetterInfo> localSetterInfoList = propertyNameAndDescList.get(index);

                    switch (varInsnNode.getOpcode()) {
                        case ILOAD:
                        case LLOAD:
                        case FLOAD:
                        case DLOAD:
                        case ALOAD:
                        {
                            BasicValue basicValue = currentFrame.getLocal(index);
                            LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterInfoList, basicValue.getType().getDescriptor());
                            if (localSetterInfo == null) {
                                throw new RuntimeException("load exception");
                            }
                            methodVisitor.visitVarInsn(ALOAD, 0);
                            methodVisitor.visitFieldInsn(GETFIELD, taskClassName, localSetterInfo.getName(), localSetterInfo.getDesc());
                            break;
                        }
                        case ISTORE:
                        case LSTORE:
                        case FSTORE:
                        case DSTORE:
                        case ASTORE:
                        {
                            BasicValue basicValue = currentFrame.getStack(currentFrame.getStackSize() - 1);
                            LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterInfoList, basicValue.getType().getDescriptor());
                            if (localSetterInfo == null) {
                                throw new RuntimeException("store exception");
                            }

                            methodVisitor.visitVarInsn(ALOAD, 0);
                            methodVisitor.visitMethodInsn(INVOKESTATIC, taskClassName, localSetterInfo.getSetterName(), localSetterInfo.getSetterDesc(), false);
                            break;
                        }
                        case RET:
                            throw new IllegalArgumentException("un support opcode ret");
                    }
                } else if (abstractInsnNode instanceof FrameNode) {
                    // FrameNode frameNode = (FrameNode) abstractInsnNode;
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
                    switch (abstractInsnNode.getOpcode()) {
                        case IRETURN:
                        case LRETURN:
                        case FRETURN:
                        case DRETURN:
                        case RETURN:
                            throw new IllegalArgumentException("async function must return Future");
                        case ARETURN:
                            {
                                AbstractInsnNode node = TransformerHelper.getPrevInsnNode(methodNode.instructions, i);
                                if (!(node instanceof MethodInsnNode)) {
                                    throw new IllegalArgumentException("async must appear in return statement");
                                }
                                MethodInsnNode mNode = (MethodInsnNode) node;
                                if (!TransformerHelper.isAsyncCall(mNode)) {
                                    throw new IllegalArgumentException("async must appear in return statement");
                                }

                                Type[] argTypes = Type.getArgumentTypes(mNode.desc);
                                Frame<BasicValue> currentFrame = frames[i];
                                if (argTypes.length == 0) {
                                    if (currentFrame != null) {
                                        int stackSize = currentFrame.getStackSize();
                                        int newMax = Math.max(stackSize + 2, currentFrame.getMaxStackSize());
                                        maxStackSize = Math.max(maxStackSize, newMax);
                                    }

                                    methodVisitor.visitVarInsn(ALOAD, 0);
                                    methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, "Lcom/whatswater/async/future/TaskFutureImpl;");
                                    methodVisitor.visitInsn(ACONST_NULL);
                                    methodVisitor.visitMethodInsn(
                                        INVOKEVIRTUAL,
                                        "com/whatswater/async/future/TaskFutureImpl",
                                        "tryComplete",
                                        "(Ljava/lang/Object;)Z",
                                        false
                                    );
                                    if (currentFrame != null) {
                                        for (int j = 0; j < currentFrame.getStackSize(); j++) {
                                            methodVisitor.visitInsn(NOP);
                                        }
                                    }
                                    methodVisitor.visitInsn(RETURN);
                                } else {
                                    if (currentFrame != null) {
                                        int stackSize = currentFrame.getStackSize();
                                        int newMax = Math.max(stackSize + 1, currentFrame.getMaxStackSize());
                                        maxStackSize = Math.max(maxStackSize, newMax);
                                    }
                                    methodVisitor.visitVarInsn(ALOAD, 0);
                                    methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, "Lcom/whatswater/async/future/TaskFutureImpl;");
                                    methodVisitor.visitMethodInsn(
                                        INVOKESTATIC,
                                        taskClassName,
                                        completeMethodNameAndDesc[0],
                                        completeMethodNameAndDesc[1],
                                        false
                                    );
                                    methodVisitor.visitInsn(RETURN);
                                }
                            }
                            break;
                        default:
                            // LASTORE等操作数组的指令，不需要特殊处理
                            methodVisitor.visitInsn(abstractInsnNode.getOpcode());
                    }
                }
            }

            // default label，抛出异常
            methodVisitor.visitLabel(defaultLabel);
            methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
            methodVisitor.visitInsn(ATHROW);

            // 通用错误处理代码
            methodVisitor.visitLabel(endLabel);
            methodVisitor.visitLabel(handlerLabel);
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, taskClassName, "_future", "Lcom/whatswater/async/future/TaskFutureImpl;");
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/whatswater/async/future/TaskFutureImpl", "tryFail", "(Ljava/lang/Throwable;)Z", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);

            // 转换过程中未引入任何本地变量
            methodVisitor.visitMaxs(maxStackSize, 3);
            methodVisitor.visitEnd();
        }
        taskClassWriter.visitEnd();
        ret.add(new GenerateClassData(taskClassWriter, taskClassName, ClassType.TASK, propertyNameAndDescList));
        return ret;
    }

    public String getPath() {
        return path;
    }
}
