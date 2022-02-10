package com.whatswater.curd.project.module.workflow.event;

public class FlowSystemEvent {
    private FlowSystemEventTypeEnum type;
    private Object context;

    public FlowSystemEventTypeEnum getType() {
        return type;
    }

    public void setType(FlowSystemEventTypeEnum type) {
        this.type = type;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
