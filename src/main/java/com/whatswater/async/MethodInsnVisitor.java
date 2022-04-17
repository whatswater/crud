package com.whatswater.async;


import org.objectweb.asm.tree.*;

public interface MethodInsnVisitor {
    void visitMethodInsnNode(MethodInsnNode methodInsnNode, int i);
    void visitFieldInsnNode(FieldInsnNode fieldInsnNode, int i);
    void visitTableSwitchInsnNode(TableSwitchInsnNode tableSwitchInsnNode, int i);
    void visitLineNumberNode(LineNumberNode lineNumberNode, int i);
    void visitIincInsnNode(IincInsnNode iincInsnNode, int i);
    void visitIntInsnNode(IntInsnNode intInsnNode, int i);
    void visitLabelNode(LabelNode labelNode, int i);
    void visitMultiANewArrayInsnNode(MultiANewArrayInsnNode multiANewArrayInsnNode, int i);
    void visitLdcInsnNode(LdcInsnNode ldcInsnNode, int i);
    void visitTypeInsnNode(TypeInsnNode typeInsnNode, int i);
    void visitVarInsn(VarInsnNode varInsnNode, int i);
    void visitInvokeDynamicInsnNode(InvokeDynamicInsnNode invokeDynamicInsnNode, int i);
    void visitFrameNode(FrameNode frameNode, int i);
    void visitJumpInsnNode(JumpInsnNode jumpInsnNode, int i);
    void visitInsnNode(InsnNode insnNode, int i);
    void visitLookupSwitchInsnNode(LookupSwitchInsnNode lookupSwitchInsnNode, int i);
}
