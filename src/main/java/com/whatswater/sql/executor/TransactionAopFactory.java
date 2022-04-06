package com.whatswater.sql.executor;


import cn.hutool.core.io.FileUtil;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import io.vertx.sqlclient.Transaction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class TransactionAopFactory {
    public <T extends TransactionService<T>> TransactionService<T> enhanceTransactionService(TransactionService<T> service) {
        ClassWriter cw = new ClassWriter(0);
        String className = service.getClass().getName();
        cw.visit(V1_1, ACC_PUBLIC, className + "$$Enhance$$", null, className, null);

        // 编译原来的构造方法，重新生成调用super的方法
        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mw.visitVarInsn(AALOAD, 0);
        mw.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V", false);
        mw.visitInsn(RETURN);
        mw.visitMaxs(1, 1);
        mw.visitEnd();

        cw.visitField(ACC_PUBLIC, "transaction", Type.getDescriptor(Transaction.class), null, null);

        byte[] data = cw.toByteArray();
        FileUtil.writeBytes(data,"D:\\code\\java\\asm_class");

        return null;
    }

    public static void main(String[] args) {
        TransactionAopFactory aopFactory = new TransactionAopFactory();
        aopFactory.enhanceTransactionService(new EmployeeService(null));
    }
}
