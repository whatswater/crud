package com.whatswater.async;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;
import static com.whatswater.async.AsyncConst.*;

public class MethodInsnTransformer {
    private MethodVisitor methodVisitor;
    private Map<Integer, List<LocalSetterInfo>> localSetterMap;
    private String taskClassName;
    private Frame<BasicValue>[] frames;
    private String futurePropertyName;

    public MethodInsnTransformer(
        MethodVisitor methodVisitor,
        Map<Integer, List<LocalSetterInfo>> localSetterMap,
        Frame<BasicValue>[] frames,
        String taskClassName,
        String futurePropertyName
    ) {
        this.methodVisitor = methodVisitor;
        this.localSetterMap = localSetterMap;
        this.frames = frames;
        this.taskClassName = taskClassName;
        this.futurePropertyName = futurePropertyName;
    }

    public int transformReturn(InsnNode insnNode, int insnOffset, InsnList insnList, String[] completeMethodNameAndDesc) {
        int maxStackSize = 0;
        switch (insnNode.getOpcode()) {
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case RETURN:
                throw new IllegalArgumentException("async function must return Future");
            case ARETURN:
            {
                AbstractInsnNode node = TransformerHelper.getPrevInsnNode(insnList, insnOffset);
                if (!(node instanceof MethodInsnNode)) {
                    throw new IllegalArgumentException("async must appear in return statement");
                }
                MethodInsnNode mNode = (MethodInsnNode) node;
                if (!TransformerHelper.isAsyncCall(mNode)) {
                    throw new IllegalArgumentException("async must appear in return statement");
                }

                Type[] argTypes = Type.getArgumentTypes(mNode.desc);
                Frame<BasicValue> currentFrame = frames[insnOffset];
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
                methodVisitor.visitInsn(insnNode.getOpcode());
        }
        return maxStackSize;
    }

    public void transformVarInsnNode(VarInsnNode varInsnNode, int insnOffset) {
        int index = varInsnNode.var;
        switch (varInsnNode.getOpcode()) {
            case ILOAD:
            case LLOAD:
            case FLOAD:
            case DLOAD:
            case ALOAD:
            {
                LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterMap, index, insnOffset);
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
                LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterMap, index, insnOffset + 2);
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
    }

    public void transformIincInsnNode(IincInsnNode iincInsnNode, int insnOffset) {
        int index = iincInsnNode.var;
        LocalSetterInfo localSetterInfo = TransformerHelper.findSetterMethod(localSetterMap, index, insnOffset);
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
    }

    /**
     * 创建awaitHandler对象并初始化，结束后栈添加一个awaitHandler对象，最大栈数量4
     * @param methodVisitor 方法
     * @param state awaitHandler跳转回来的state
     */
    private void newAwaitHandler(MethodVisitor methodVisitor, int state) {
        methodVisitor.visitTypeInsn(NEW, HANDLER_CLASS_NAME);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 0);
        switch (state) {
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
                if (state <= Byte.MAX_VALUE) {
                    methodVisitor.visitIntInsn(BIPUSH, state);
                } else if (state <= Short.MAX_VALUE) {
                    methodVisitor.visitIntInsn(SIPUSH, state);
                } else {
                    methodVisitor.visitLdcInsn(state);
                }
        }
        methodVisitor.visitMethodInsn(INVOKESPECIAL, HANDLER_CLASS_NAME, INIT_METHOD_NAME, HANDLER_CONSTRUCTOR_DESC, false);
    }


    /**
     * 创建并且将awaitHandler放到task的handlerPropertyName上，结束后消耗栈顶一个future对象，最大栈数量5
     * @param state awaitHandler跳转回来的state
     */
    public void createAndPutHandler(int state, String handlerPropertyName) {
        // 原先的栈顶，为一个Future对象
        // 创建AwaitTaskHandler对象，并传入this和labelIndex，将新创建的AwaitTaskHandler设置为handlerPropertyName属性
        methodVisitor.visitVarInsn(ALOAD, 0);
        newAwaitHandler(methodVisitor, state);
        methodVisitor.visitFieldInsn(PUTFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD,  taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
        // 栈顶的对象在此使用，调用onComplete后，栈顶存在一个新的Future对象
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, FUTURE_INTERFACE_NAME, "onComplete", "(Lio/vertx/core/Handler;)Lio/vertx/core/Future;", true);
        // 丢失掉新的Future对象
        methodVisitor.visitInsn(POP);
    }

    /**
     * awaitReturn后的处理
     * 判断异步是否成功，成功则在栈顶放入异步任务的结果，失败则抛出异常
     * 最大栈1，成功后栈顶添加异步任务的结果，失败后栈无变化
     */
    public void onAwaitReturn(String handlerPropertyName) {
        // 判断执行结果是否成功
        Label toLabel = new Label();
        Label elseLabel = new Label();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "succeeded", "()Z", false);
        methodVisitor.visitJumpInsn(IFEQ, elseLabel);
        // max1 stack0

        // 成功后执行
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "getResult", "()Ljava/lang/Object;", false);
        methodVisitor.visitJumpInsn(GOTO, toLabel);
        // max1 stack1

        // 失败后执行
        methodVisitor.visitLabel(elseLabel);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, handlerPropertyName, HANDLER_FIELD_DESC);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, HANDLER_CLASS_NAME, "getThrowable", "()Ljava/lang/Throwable;", false);
        methodVisitor.visitInsn(ATHROW);
        methodVisitor.visitLabel(toLabel);
        // max1 stack0
    }

    public void exceptionHandler(Label handlerLabel) {
        methodVisitor.visitLabel(handlerLabel);
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, taskClassName, futurePropertyName, FUTURE_FIELD_DESC);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, FUTURE_CLASS_NAME, "tryFail", "(Ljava/lang/Throwable;)Z", false);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitInsn(RETURN);
    }
}
