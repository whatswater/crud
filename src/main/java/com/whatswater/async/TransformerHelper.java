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


public abstract class TransformerHelper {
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

    public static Frame<BasicValue>[] computeFrame(String className, MethodNode methodNode) throws AnalyzerException {
        Analyzer<BasicValue> analyzer = new Analyzer<>(new TypeInterpreter());
        return analyzer.analyze(className, methodNode);
    }

    public static GenerateClassData findTaskClass(List<GenerateClassData> classDataList) {
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

    public static LocalSetterInfo findSetterMethod(Map<Integer, List<LocalSetterInfo>> localSetterMap, int idx, int insnOffset) {
        List<LocalSetterInfo> localSetterInfoList = localSetterMap.get(idx);
        if (localSetterInfoList == null) {
            return null;
        }
        for (LocalSetterInfo localSetterInfo: localSetterInfoList) {
            int startIndex = localSetterInfo.getStartIndex();
            int endIndex = localSetterInfo.getEndIndex();

            if (insnOffset > startIndex && insnOffset < endIndex) {
                return localSetterInfo;
            }
        }
        return null;
    }

    public static LocalSetterInfo findFirstSetterMethod(Map<Integer, List<LocalSetterInfo>> localSetterMap, int idx) {
        List<LocalSetterInfo> localSetterInfoList = localSetterMap.get(idx);
        if (localSetterInfoList == null || localSetterInfoList.isEmpty()) {
            return null;
        }
        return localSetterInfoList.get(0);
    }

    public static String classPathDesc(String classPath) {
        return "L" + classPath + ";";
    }

    public static FieldNode findFieldNode(String name, ClassNode classNode) {
        List<FieldNode> fieldNodes = classNode.fields;
        for (FieldNode fieldNode: fieldNodes) {
            if (fieldNode.name.equals(name)) {
                return fieldNode;
            }
        }
        return null;
    }

    public static MethodNode findMethodNode(String name, String desc, ClassNode classNode) {
        List<MethodNode> methodNodes = classNode.methods;
        for (MethodNode methodNode: methodNodes) {
            if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
                return methodNode;
            }
        }
        return null;
    }

    public static boolean isSynchronizedMethod(MethodNode methodNode) {
        return Modifier.isSynchronized(methodNode.access);
    }

    public static boolean needTransformAwait(MethodNode methodNode) {
        if (isSynchronizedMethod(methodNode)) {
            return false;
        }

        for (AbstractInsnNode abstractInsnNode: methodNode.instructions) {
            // 判断方法的同步块内是否存在await调用
            // 暂时只判断是否存在syn块，不看块里面有没有
            if (abstractInsnNode.getOpcode() == Opcodes.MONITORENTER) {
                return false;
            }
        }
        List<AnnotationNode> annotationNodeList = methodNode.visibleAnnotations;
        if (annotationNodeList != null) {
            for (AnnotationNode annotationNode: annotationNodeList) {
                Type annotationType = Type.getType(annotationNode.desc);
                if (annotationType.getClassName().equals("com.whatswater.async.type.NoTransformAwait")) {
                    return false;
                }
            }
        }
        return true;
    }
}
