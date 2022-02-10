package com.whatswater.curd.project.module.workflow.flowEngine.context;


import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;

public class ContextFlowLinkStart {
    private boolean back;
    private FlowInstanceTask prevTask;

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public FlowInstanceTask getPrevTask() {
        return prevTask;
    }

    public void setPrevTask(FlowInstanceTask prevTask) {
        this.prevTask = prevTask;
    }
}
