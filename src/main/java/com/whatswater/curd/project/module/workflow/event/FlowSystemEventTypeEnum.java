package com.whatswater.curd.project.module.workflow.event;


public enum FlowSystemEventTypeEnum {
    GENERATE_TASK("/business/todo/onTaskCreated"),
    CANCEL_TASK("/business/todo/onTaskCanceled"),
    COMPLETE_TASK("/business/todo/onTaskCompleted"),
    BACK_TASK("/business/todo/onTaskBack"),
    ;
    private final String url;

    FlowSystemEventTypeEnum(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
