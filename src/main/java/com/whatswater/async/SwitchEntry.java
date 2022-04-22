package com.whatswater.async;


import org.objectweb.asm.Label;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;


public class SwitchEntry {
    private int state;
    private Label resumeLabel;
    private Frame<BasicValue> currentFrame;

    public SwitchEntry(int state) {
        this.state = state;
        this.resumeLabel = new Label();
    }

    public int getState() {
        return state;
    }

    public Label getResumeLabel() {
        return resumeLabel;
    }

    public Frame<BasicValue> getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(Frame<BasicValue> currentFrame) {
        this.currentFrame = currentFrame;
    }
}
