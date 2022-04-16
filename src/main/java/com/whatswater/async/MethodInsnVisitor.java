package com.whatswater.async;


import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public interface MethodInsnVisitor {
    void visitMethodInsnNode(MethodInsnNode methodInsnNode, Frame<BasicValue> frame);
    void visitFieldInsnNode(FieldInsnNode fieldInsnNode, Frame<BasicValue> frame);
    void visitTableSwitchInsnNode(TableSwitchInsnNode tableSwitchInsnNode, Frame<BasicValue> frame);
    void visitLineNumberNode(LineNumberNode lineNumberNode, Frame<BasicValue> frame);
    void visitIincInsnNode(IincInsnNode iincInsnNode, Frame<BasicValue> frame);
    void visitIntInsnNode(IntInsnNode intInsnNode, Frame<BasicValue> frame);
    void visitLabelNode(LabelNode labelNode, Frame<BasicValue> frame);
    void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode multiANewArrayInsnNode, Frame<BasicValue> frame);
    void visitLdcInsnNode(LdcInsnNode ldcInsnNode, Frame<BasicValue> frame);
    void visitTypeInsnNode(TypeInsnNode typeInsnNode, Frame<BasicValue> frame);
    void visitVarInsn(VarInsnNode varInsnNode, Frame<BasicValue> frame);
    void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode invokeDynamicInsnNode, Frame<BasicValue> frame);
    void visitFrameNode(FrameNode frameNode, Frame<BasicValue> frame);
    void visitJumpInsnNode(JumpInsnNode jumpInsnNode, Frame<BasicValue> frame);
    void visitInsnNode(InsnNode insnNode, Frame<BasicValue> frame);
    void visitLookupSwitchInsnNode(LookupSwitchInsnNode lookupSwitchInsnNode, Frame<BasicValue> frame);
}
