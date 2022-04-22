package com.whatswater.async;


import com.whatswater.async.GenerateClassData.ClassType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.whatswater.async.AsyncConst.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.GETFIELD;

public class SwitchInfo {
    Label defaultLabel;
    List<SwitchEntry> switchEntryList;

    public SwitchInfo(int count) {
        List<SwitchEntry> switchEntryList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            switchEntryList.add(new SwitchEntry(i));
        }
        this.switchEntryList = switchEntryList;
        this.defaultLabel = new Label();
    }

    public Label[] getSwitchLabelList() {
        return switchEntryList.stream().map(SwitchEntry::getResumeLabel).toArray(Label[]::new);
    }

    public Label getResumeLabel(int i) {
        return switchEntryList.get(i).getResumeLabel();
    }

    public void setFrame(int i, Frame<BasicValue> frame) {
        this.switchEntryList.get(i).setCurrentFrame(frame);
    }

    public void throwExceptionDefaultLabel(MethodVisitor methodVisitor) {
        methodVisitor.visitLabel(defaultLabel);
        methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
        methodVisitor.visitInsn(ATHROW);
    }

    public GenerateClassData saveAndRestoreStack(
        int state,
        ClassNode classNode,
        MethodVisitor methodVisitor,
        String stackClassName,
        String taskClassName,
        String stackHolderPropertyName
    ) {
        SwitchEntry switchEntry = switchEntryList.get(state);
        ClassWriter stackClassWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        Map<Integer, String[]> stackHolderNameList = Transformer.generateStackMapHolder(classNode, stackClassWriter, stackClassName, switchEntry.getCurrentFrame());

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitTypeInsn(NEW, stackClassName);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, stackClassName, INIT_METHOD_NAME, EMPTY_CONSTRUCTOR_DESC, false);
        methodVisitor.visitFieldInsn(PUTFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);

        Frame<BasicValue> currentFrame = switchEntry.getCurrentFrame();
        // 保存栈数据
        for (int index = currentFrame.getStackSize() - 2; index >= 0; index--) {
            String[] names = stackHolderNameList.get(index);
            if (names == null) {
                continue;
            }

            String setterName = names[2];
            String setterDesc = names[3];
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);
            methodVisitor.visitTypeInsn(CHECKCAST, stackClassName);
            methodVisitor.visitMethodInsn(INVOKESTATIC, stackClassName, setterName, setterDesc, false);
        }
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitLabel(getResumeLabel(state));

        // 恢复栈数据
        for (int index = 0; index < currentFrame.getStackSize() - 1; index++) {
            String[] names = stackHolderNameList.get(index);
            if (names == null) {
                continue;
            }

            String propertyName = names[0];
            String propertyDesc = names[1];
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, taskClassName, stackHolderPropertyName, OBJECT_CLASS_DESC);
            methodVisitor.visitTypeInsn(CHECKCAST, stackClassName);
            methodVisitor.visitFieldInsn(GETFIELD, stackClassName, propertyName, propertyDesc);
        }
        return new GenerateClassData(stackClassWriter, stackClassName, ClassType.STACK_HOLDER, null);
    }
}
