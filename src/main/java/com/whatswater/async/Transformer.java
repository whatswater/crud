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

// TODO
// 1、处理内部类权限问题
// 2、优化代码
// 3、开发maven插件
public class Transformer {
    public static final String SETTER_PREFIX = "set_";
    public static final String OBJECT_CLASS_NAME = "java/lang/Object";
    public static final String OBJECT_CLASS_DESC = "Ljava/lang/Object;";
    public static final String THROWABLE_CLASS_NAME = "java/lang/Throwable";
    public static final String INIT_METHOD_NAME = "<init>";
    public static final String EMPTY_CONSTRUCTOR_DESC = "()V";

    public static final String TASK_INTERFACE_NAME = "com/whatswater/async/Task";
    public static final String TASK_CLASS_NAME = "Task";
    public static final String JAVA_FILE_SUFFIX = ".java";

    public static final String METHOD_NAME_MOVE_TO_NEXT = "moveToNext";
    public static final String METHOD_DESC_MOVE_TO_NEXT = "(I)V";

    public static final String HANDLER_CLASS_NAME = "com/whatswater/async/handler/AwaitTaskHandler";
    public static final String HANDLER_FIELD_DESC = "Lcom/whatswater/async/handler/AwaitTaskHandler;";
    public static final String HANDLER_CONSTRUCTOR_DESC = "(Lcom/whatswater/async/Task;I)V";

    public static final String FUTURE_INTERFACE_NAME = "io/vertx/core/Future";
    public static final String FUTURE_CLASS_NAME = "com/whatswater/async/future/TaskFutureImpl";
    public static final String FUTURE_FIELD_DESC = "Lcom/whatswater/async/future/TaskFutureImpl;";

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
                List<GenerateClassData> classDataList = generateClass(suffix, methodNode);
                MethodNode replacement = generateCallTaskMethod(classNode, methodNode, classDataList);
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
                        methodVisitor.visitTypeInsn(NEW, HANDLER_CLASS_NAME);
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
                        methodVisitor.visitMethodInsn(INVOKESPECIAL, HANDLER_CLASS_NAME, INIT_METHOD_NAME, HANDLER_CONSTRUCTOR_DESC, false);
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
                            Map<Integer, String[]> stackHolderNameList = generateStackMapHolder(classNode, stackClassWriter, stackClassName, currentFrame);
                            ret.add(new GenerateClassData(stackClassWriter, stackClassName, ClassType.STACK_HOLDER, null));

                            methodVisitor.visitVarInsn(ALOAD, 0);
                            methodVisitor.visitTypeInsn(NEW, stackClassName);
                            methodVisitor.visitInsn(DUP);
                            methodVisitor.visitMethodInsn(INVOKESPECIAL, stackClassName, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
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
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "succeeded", "()Z", false);
                        methodVisitor.visitJumpInsn(IFEQ, elseLabel);

                        // 成功后执行
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "getResult", "()Ljava/lang/Object;", false);
                        methodVisitor.visitJumpInsn(GOTO, toLabel);

                        // 失败后执行
                        methodVisitor.visitLabel(elseLabel);
                        methodVisitor.visitVarInsn(ALOAD, 0);
                        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
                        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "getThrowable", "()Ljava/lang/Throwable;", false);
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
                    List<LocalSetterInfo> localSetterInfoList = localSetterMap.get(index);
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
                    List<LocalSetterInfo> localSetterInfoList = localSetterMap.get(index);

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
                                    methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, FUTURE_FIELD_DESC);
                                    methodVisitor.visitInsn(ACONST_NULL);
                                    methodVisitor.visitMethodInsn(
                                        INVOKEVIRTUAL,
                                        FUTURE_CLASS_NAME,
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
                                    methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, FUTURE_FIELD_DESC);
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
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
            methodVisitor.visitInsn(ATHROW);

            // 通用错误处理代码
            methodVisitor.visitLabel(endLabel);
            methodVisitor.visitLabel(handlerLabel);
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, FUTURE_FIELD_DESC);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, FUTURE_CLASS_NAME, "tryFail", "(Ljava/lang/Throwable;)Z", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);

            // 转换过程中未引入任何本地变量
            methodVisitor.visitMaxs(maxStackSize, 3);
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

            String setterName = SETTER_PREFIX + name;
            String setterDesc = "(" + desc + classDesc + ")V";
            LocalSetterInfo setterInfo = new LocalSetterInfo(name, desc, setterName, setterDesc);
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
            "tryComplete",
            "(Ljava/lang/Object;)Z",
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
        classWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + Transformer.JAVA_FILE_SUFFIX, null);
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
            List<LocalSetterInfo> names = propertyNames.get(0);
            LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(names, "L" + classNode.name + ";");
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

            List<LocalSetterInfo> names = propertyNames.get(i + argOffset);
            LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(names, argType.getDescriptor());
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
        replacement.visitMaxs(idx > argOffset + argTypes.length ? 3 : 2, idx + 1);
        replacement.visitEnd();
        return replacement;
    }
}
