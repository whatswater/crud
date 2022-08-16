package com.whatswater.sql.executor;


import org.objectweb.asm.*;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.Opcodes.*;

public class ContextServiceFactory {
    public static final String SIGN = "whatswater";
    public static final String OBJECT_CLASS_NAME = "java/lang/Object";
    public static final char CLASS_NAME_SPLIT_CHAR = '/';
    public static final String SUFFIX_ENHANCE = "$$Enhance$$Proxy";
    public static final String SUFFIX_ENHANCE_JAVA = SUFFIX_ENHANCE + ".java";
    public static final String CONTEXT_FIELD_NAME = "context";
    public static final String CONTEXT_FIELD_DESCRIPTOR = "L" + "com.whatswater.sql.executor.Context" + ";";
    public static final String SERVICE_FIELD_NAME = "service";
    public static final String CONTEXT_INTERFACE_NAME = "";
    public static final String CONTEXT_CLASS_NAME = "";


    public <T> T enhanceService(T origin, Class<T> interfaceCls) {
        Class<?> cls = origin.getClass();
        ClassNode interfaceNode = readClass(interfaceCls);
        ClassNode classNode = readClass(cls);

        if (interfaceNode == null || classNode == null) {
            return origin;
        }

        ClassWriter classWriter = initWriter(interfaceNode, classNode);
        createGetServiceMethod(classWriter, classNode);
        copyInterfaceMethod(classWriter, interfaceNode, classNode);
//        Class<T> enhanceClass = (Class<T>) loadEnhanceClass(interfaceCls, classWriter);
//        return enhanceClass.newInstance();
        return null;
    }

    private void copyInterfaceMethod(ClassWriter classWriter, ClassNode interfaceNode, ClassNode classNode) {
        for (MethodNode interfaceName: interfaceNode.methods) {
            MethodVisitor methodVisitor = classWriter.visitMethod(interfaceName.access, interfaceName.name, interfaceName.desc, interfaceName.signature, interfaceName.exceptions.toArray(new String[0]));
            for (MethodNode classMethod: classNode.methods) {
                final boolean match = classMethod.name.equals(interfaceName.name)
                    && classMethod.desc.equals(interfaceName.desc);
                if (match) {
                    copyMethod(methodVisitor, classMethod);
                }
            }
        }
    }

    private void copyMethod(MethodVisitor methodVisitor, MethodNode methodNode) {
        methodVisitor.visitCode();
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof FieldInsnNode) {
                FieldInsnNode fieldInsnNode = (FieldInsnNode) instruction;
                instruction.accept(methodVisitor);
                if (fieldInsnNode.getOpcode() != GETFIELD) {
                    continue;
                }
                // TODO 分析当前栈顶是不是this
                // 1、判断执行到当前指令的所有分支，找到一个分支模拟执行，判断栈顶是不是this
                // TODO 转换成get service调用
            } else {
                instruction.accept(methodVisitor);
            }
        }
        methodVisitor.visitEnd();
    }

    private void createGetServiceMethod(ClassWriter classWriter, ClassNode classNode) {
        String desc = "L" + classNode.name + ";";
        MethodVisitor methodVisitor = classWriter.visitMethod(
            ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
            "get$Service$",
            "(Ljava/lang/Object;" + desc + ")Ljava/lang/Object;",
            null,
            null
        );


        methodVisitor.visitCode();
        Label w = new Label();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitTypeInsn(INSTANCEOF, CONTEXT_INTERFACE_NAME);
        methodVisitor.visitJumpInsn(IFEQ, w);
        methodVisitor.visitLabel(w);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitInsn(ARETURN);

        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitFieldInsn(GETFIELD, classNode.name, CONTEXT_FIELD_NAME, CONTEXT_FIELD_DESCRIPTOR);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, CONTEXT_CLASS_NAME, "getService", "()V", false);
        methodVisitor.visitInsn(ARETURN);
    }

    private Class<?> loadEnhanceClass(Class<?> interfaceCls, ClassWriter classWriter) {
        return null;
    }

    private static ClassWriter initWriter(ClassNode interfaceNode, ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(0);

        String enhanceClassName = classNode.name + SUFFIX_ENHANCE;
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, enhanceClassName, null, OBJECT_CLASS_NAME,  new String[] { interfaceNode.name });
        String simpleName = classNode.name.substring(classNode.name.lastIndexOf(CLASS_NAME_SPLIT_CHAR) + 1);
        classWriter.visitSource(simpleName + SUFFIX_ENHANCE_JAVA, SIGN);
        initField(classWriter, classNode, enhanceClassName);

        return classWriter;
    }

    private static void initField(ClassWriter classWriter, ClassNode classNode, String enhanceClassName) {
        {
            FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE, CONTEXT_FIELD_NAME, CONTEXT_FIELD_DESCRIPTOR, null, null);
            fieldVisitor.visitEnd();

            String setterName = "setter$" + CONTEXT_FIELD_NAME + "$";
            String setterDesc = "(" + CONTEXT_FIELD_DESCRIPTOR + ")V";

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, setterName, setterDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, enhanceClassName, CONTEXT_FIELD_NAME, CONTEXT_FIELD_DESCRIPTOR);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            String desc = "L" + classNode.name + ";";
            FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE, SERVICE_FIELD_NAME, desc, null, null);
            fieldVisitor.visitEnd();

            String setterName = "setter$" + SERVICE_FIELD_NAME + "$";
            String setterDesc = "(" + desc + ")V";

            MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, setterName, setterDesc, null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, enhanceClassName, SERVICE_FIELD_NAME, desc);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
    }

    private static ClassNode readClass(Class<?> cls) {
        String resourcePath = "/" + cls.getName().replaceAll("\\.", "/");
        InputStream inputStream = cls.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            return null;
        }
        try {
            ClassReader classReader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode(ASM9);
            classReader.accept(classNode, ClassReader.SKIP_DEBUG);
            return classNode;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
