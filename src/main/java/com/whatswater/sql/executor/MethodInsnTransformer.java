package com.whatswater.sql.executor;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.util.List;

public class MethodInsnTransformer {
    private MethodVisitor methodVisitor;

    public void transformInsnList(List<AbstractInsnNode> insnNodeList) {
        for (AbstractInsnNode abstractInsnNode : insnNodeList) {
            if (abstractInsnNode instanceof MethodInsnNode) {
                transformMethodInsn((MethodInsnNode) abstractInsnNode);
            } else if (abstractInsnNode instanceof LabelNode) {
                transformLabel((LabelNode) abstractInsnNode);
            } else if (abstractInsnNode instanceof TableSwitchInsnNode) {

            }
        }
    }

    public void transformMethodInsn(MethodInsnNode methodInsnNode) {
        methodVisitor.visitMethodInsn(
            methodInsnNode.getOpcode(),
            methodInsnNode.owner,
            methodInsnNode.name,
            methodInsnNode.desc,
            methodInsnNode.itf
        );
    }

    public void transformLabel(LabelNode labelNode) {
        methodVisitor.visitLabel(labelNode.getLabel());
    }

    public void transformTableSwitchInsn() {

    }
}
