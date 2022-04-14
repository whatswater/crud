package com.whatswater.sql.executor;


import com.whatswater.curd.project.sys.employee.EmployeeService;
import org.objectweb.asm.*;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

public class SqlSessionAopFactory {
    public static final char CLASS_NAME_SPLIT_CHAR = '/';
    public static final String SUFFIX_ENHANCE = "$$Enhance$$";
    public static final String SUFFIX_ENHANCE_JAVA = SUFFIX_ENHANCE + ".java";

    public static byte[] enhanceTransactionServiceClass(String path) throws IOException {
        ClassInfo classInfo = ClassInfo.getClassInfo(path);
        String className = classInfo.getClassName();
        String simpleName = className.substring(className.lastIndexOf(CLASS_NAME_SPLIT_CHAR) + 1);

        String sqlSessionFieldName = "sqlSession";
        String sqlSessionFieldDescriptor = "L" + "com.whatswater.sql.executor.SqlSession" + ";";
        String sqlSessionMethodParameterDescriptor = "(L" + "com.whatswater.sql.executor.SqlSession" + ";)";

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;
        String enhanceClassName = className + SUFFIX_ENHANCE;
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, enhanceClassName, null, className, null);
        classWriter.visitSource(simpleName + SUFFIX_ENHANCE_JAVA, null);
        {
            FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE, sqlSessionFieldName, sqlSessionFieldDescriptor, null, null);
            fieldVisitor.visitEnd();
        }
        String enhanceClassNameDescriptor = "L" + enhanceClassName;
        String classNameDescriptor = "L" + className;
        {
            ClassInfo.copyConstruct(classInfo, classWriter);
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "bindNewSqlSession", sqlSessionMethodParameterDescriptor + classNameDescriptor + ";", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(16, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(PUTFIELD, enhanceClassName, sqlSessionFieldName, sqlSessionFieldDescriptor);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(17, label1);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitInsn(ARETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", enhanceClassNameDescriptor, null, label0, label2, 0);
            methodVisitor.visitLocalVariable(sqlSessionFieldName, sqlSessionFieldDescriptor, null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getCurrentSqlSession", "()" + sqlSessionFieldDescriptor, null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(22, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, enhanceClassName, sqlSessionFieldName, sqlSessionFieldDescriptor);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", enhanceClassNameDescriptor, null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_BRIDGE | ACC_SYNTHETIC, "bindNewSqlSession", sqlSessionMethodParameterDescriptor + "Ljava/lang/Object;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(7, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, enhanceClassName, "bindNewSqlSession",  sqlSessionMethodParameterDescriptor + classNameDescriptor + ";", false);
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", enhanceClassNameDescriptor, null, label0, label1, 0);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        return classWriter.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        SqlSessionAopFactory.enhanceTransactionServiceClass(EmployeeService.class.getName().replaceAll("\\.", "/"));
    }
}
