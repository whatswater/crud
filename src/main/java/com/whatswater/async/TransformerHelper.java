package com.whatswater.async;


import com.whatswater.async.GenerateClassData.ClassType;
import com.whatswater.async.frame.TypeInterpreter;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

public abstract class TransformerHelper {
    public static final String SETTER_PREFIX = "set_";

    public static char[] MAPPING_SPACE = new char[] {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '_'
    };
    public static final int MAPPING_SPACE_LENGTH = MAPPING_SPACE.length;
    public static final int HASH_CODE = TransformerHelper.class.getClassLoader().hashCode();
    public static final String HASH_CODE_STRING = encodeIntValue(HASH_CODE).toString();
    public static final int V1 = 1 << 30;
    public static final int MOD = V1 % MAPPING_SPACE_LENGTH;
    private static final AtomicInteger classCountHolder = new AtomicInteger(0);

    private static String encodeClassId(int idx) {
        return "_" + encodeIntValue(idx).append(HASH_CODE_STRING).toString();
    }

    private static StringBuilder encodeIntValue(int idx) {
        StringBuilder stringBuilder = new StringBuilder();
        if (idx < 0) {
            int right = idx & (0x7FFF_FFFF);
            int rightMod = right % MAPPING_SPACE_LENGTH;
            int accMod = rightMod + (MOD << 1);
            int mod = accMod % MAPPING_SPACE_LENGTH;
            int newValue = (V1 / MAPPING_SPACE_LENGTH) * 2 + right / MAPPING_SPACE_LENGTH + accMod / MAPPING_SPACE_LENGTH;
            // 此时newValue一定大于0，所以不判断
            char firstChar = MAPPING_SPACE[mod];
            stringBuilder.append(firstChar);
            transform(stringBuilder, newValue);
        } else {
            transform(stringBuilder, idx);
        }
        return stringBuilder;
    }

    private static void transform(StringBuilder stringBuilder, int value) {
        int newValue = value;
        while (newValue > 0) {
            int idx = newValue % MAPPING_SPACE_LENGTH;
            newValue = newValue / MAPPING_SPACE_LENGTH;

            char c = MAPPING_SPACE[idx];
            stringBuilder.append(c);
        }
    }

    public static String nextClassSuffix() {
        int id = classCountHolder.incrementAndGet();
        return TransformerHelper.encodeClassId(id);
    }

    private static final String ASYNC_CLASS_NAME = "com/whatswater/async/type/Async";
    public static final String AWAIT_FUNCTION_NAME = "await";
    public static final String ASYNC_FUNCTION_NAME = "async";
    private static final String FUTURE_CLASS_NAME = "io/vertx/core/Future";

    public static boolean isAsyncMethod(MethodNode methodNode) {
        return isReturnFuture(methodNode) && hasAwaitCall(methodNode);
    }

    public static boolean isReturnFuture(MethodNode methodNode) {
        String desc = methodNode.desc;
        Type type = Type.getReturnType(desc);

        if (type.getSort() != Type.OBJECT) {
            return false;
        }
        final String retTypeName = type.getInternalName();
        return FUTURE_CLASS_NAME.equals(retTypeName);
    }

    public static boolean hasAwaitCall(MethodNode methodNode) {
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                if (isAwaitCall(methodInsnNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAwaitCall(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC
            && ASYNC_CLASS_NAME.equals(methodInsnNode.owner)
            && AWAIT_FUNCTION_NAME.equals(methodInsnNode.name);
    }

    public static boolean isAsyncCall(MethodInsnNode methodInsnNode) {
        return methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC
            && ASYNC_CLASS_NAME.equals(methodInsnNode.owner)
            && ASYNC_FUNCTION_NAME.equals(methodInsnNode.name);
    }

    public static int awaitInvokeCount(MethodNode methodNode) {
        int count = 0;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if ((instruction instanceof MethodInsnNode) && isAwaitCall((MethodInsnNode) instruction)) {
                count++;
            }
        }
        return count;
    }

    public static void addEmptyConstructor(ClassWriter classWriter, String className, String futurePropertyName) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitTypeInsn(NEW, "com/whatswater/async/future/TaskFutureImpl");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/whatswater/async/future/TaskFutureImpl", "<init>", "()V", false);
        methodVisitor.visitFieldInsn(PUTFIELD, className, futurePropertyName, "Lcom/whatswater/async/future/TaskFutureImpl;");

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(3, 1);
        methodVisitor.visitEnd();
    }

    /**
     * 将本地变量表复制到Task类，并生成setter方法
     * @param className 方法所在的类
     * @param methodNode 方法
     * @param classWriter classWriter
     * @return 本地变量index => name、desc、setter、setter desc
     */
    public static Map<Integer, List<LocalSetterInfo>> copyLocalVariablesToProperties(String className, MethodNode methodNode, ClassWriter classWriter) {
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
            "com/whatswater/async/future/TaskFutureImpl",
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

    public static String addFutureProperty(ClassWriter classWriter) {
        String name = "_future";
        String desc = "Lcom/whatswater/async/future/TaskFutureImpl;";
        FieldVisitor fieldVisitor = classWriter.visitField(
            ACC_PUBLIC,
            name,
            desc,
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
            "Ljava/lang/Object;",
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
            "Lcom/whatswater/async/handler/AwaitTaskHandler;",
            null,  // 泛型签名,
            null
        );
        fieldVisitor.visitEnd();

        return name;
    }

    public static Frame<BasicValue>[] computeFrame(String className, MethodNode methodNode) throws AnalyzerException {
        Analyzer<BasicValue> analyzer = new Analyzer<>(new TypeInterpreter());
        return analyzer.analyze(className, methodNode);
    }

    public static Map<Integer, String[]> generateStackMapHolder(ClassNode classNode, ClassWriter classWriter, String className, Frame<BasicValue> currentFrame) {
        Map<Integer, String[]> propertyNameList = new TreeMap<>();

        String classDesc = "L" + className + ";";
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, className, null, "java/lang/Object", null);
        classWriter.visitSource(TransformerHelper.getSimpleNameByClassName(classNode.name) + Transformer.JAVA_FILE_SUFFIX, null);
        {
            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
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

        GenerateClassData taskClassData = findTaskClass(classDataList);
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
        replacement.visitMethodInsn(INVOKESPECIAL, taskClassName, "<init>", "()V", false);
        replacement.visitVarInsn(ASTORE, idx);

        // 将this赋值到Task
        if (argOffset > 0) {
            List<LocalSetterInfo> names = propertyNames.get(0);
            LocalSetterInfo localSetterInfo = findSetterMethod(names, "L" + classNode.name + ";");
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
            LocalSetterInfo localSetterInfo = findSetterMethod(names, argType.getDescriptor());
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
        replacement.visitMethodInsn(INVOKEVIRTUAL, taskClassName, "moveToNext", "(I)V", false);
        // 获取_future属性
        replacement.visitVarInsn(ALOAD, idx);
        replacement.visitFieldInsn(GETFIELD, taskClassName, "_future", "Lcom/whatswater/async/future/TaskFutureImpl;");

        String returnClassName = type.getReturnType().getInternalName();
        if (!"com/whatswater/async/future/TaskFutureImpl".equals(returnClassName)) {
            replacement.visitTypeInsn(CHECKCAST, returnClassName);
        }
        replacement.visitInsn(ARETURN);
        replacement.visitMaxs(idx > argOffset + argTypes.length ? 3 : 2, idx + 1);
        replacement.visitEnd();
        return replacement;
    }

    private static GenerateClassData findTaskClass(List<GenerateClassData> classDataList) {
        for (GenerateClassData classData: classDataList) {
            if (ClassType.TASK == classData.getClassType()) {
                return classData;
            }
        }
        return null;
    }

    public static String getPackageByClassName(String className) {
        if (className.lastIndexOf('/') < 0) {
            return "";
        }
        return className.substring(0, className.lastIndexOf('/') + 1);
    }

    public static String getSimpleNameByClassName(String className) {
        if (className.lastIndexOf('/') < 0) {
            return className;
        }
        return className.substring(className.lastIndexOf('/') + 1, className.length());
    }

    public static AbstractInsnNode getPrevInsnNode(InsnList insnList, int i) {
        while (i - 1 >= 0) {
            i--;
            AbstractInsnNode abstractInsnNode = insnList.get(i);
            if (abstractInsnNode.getOpcode() > 0) {
                return abstractInsnNode;
            }
        }
        return null;
    }

    public static LocalSetterInfo findSetterMethod(List<LocalSetterInfo> localSetterInfoList, String needDesc) {
        for (LocalSetterInfo localSetterInfo: localSetterInfoList) {
            String desc = localSetterInfo.getDesc();
            if (desc.equals(needDesc)) {
                return localSetterInfo;
            }
        }
        return null;
    }

    public static String classPathDesc(String classPath) {
        return "L" + classPath + ";";
    }
}
