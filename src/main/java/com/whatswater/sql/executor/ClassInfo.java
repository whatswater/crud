package com.whatswater.sql.executor;


import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class ClassInfo extends ClassVisitor {
    private String className;
    private int access;
    private int version;
    private String signature;
    private String superName;
    private String[] interfaces;
    private List<MethodInfo> methodInfoList = new LinkedList<>();

    protected ClassInfo(int api) {
        super(api);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        this.className = name;
        this.version = version;
        this.access = access;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setAccess(access);
        methodInfo.setName(name);
        methodInfo.setDescriptor(descriptor);
        methodInfo.setSignature(signature);
        methodInfo.setExceptions(exceptions);
        methodInfoList.add(methodInfo);

        return null;
    }

    public String getClassName() {
        return className;
    }

    public int getAccess() {
        return access;
    }

    public int getVersion() {
        return version;
    }

    public String getSignature() {
        return signature;
    }

    public String getSuperName() {
        return superName;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public static ClassInfo getClassInfo(String path) throws IOException {
        ClassInfo classInfo = new ClassInfo(ASM9);
        ClassReader classReader = new ClassReader(path);
        classReader.accept(classInfo, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        return classInfo;
    }

    public static void copyConstruct(ClassInfo classInfo, ClassWriter classWriter) {
        for (MethodInfo methodInfo: classInfo.methodInfoList) {
            if ("<init>".equals(methodInfo.getName()) && (!Modifier.isPrivate(methodInfo.getAccess()))) {
                MethodVisitor methodVisitor = classWriter.visitMethod(
                    methodInfo.getAccess(),
                    methodInfo.getName(),
                    methodInfo.getDescriptor(),
                    methodInfo.getSignature(),
                    methodInfo.getExceptions()
                );
                methodVisitor.visitCode();
                methodVisitor.visitVarInsn(ALOAD, 0);
                Type[] argTypes = Type.getArgumentTypes(methodInfo.getDescriptor());
                for (int i = 0; i < argTypes.length; i++) {
                    Type argType = argTypes[i];
                    methodVisitor.visitVarInsn(argType.getOpcode(ILOAD), i + 1);
                }
                methodVisitor.visitMethodInsn(INVOKESPECIAL, classInfo.className, methodInfo.getName(), methodInfo.getDescriptor(), false);
                methodVisitor.visitInsn(RETURN);
                methodVisitor.visitMaxs(argTypes.length + 1,  argTypes.length + 1);
                methodVisitor.visitEnd();
            }
        }

    }

    public class MethodInfo {
        private int access;
        private String name;
        private String descriptor;
        private String signature;
        private String[] exceptions;

        public int getAccess() {
            return access;
        }

        public void setAccess(int access) {
            this.access = access;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String[] getExceptions() {
            return exceptions;
        }

        public void setExceptions(String[] exceptions) {
            this.exceptions = exceptions;
        }
    }
}
