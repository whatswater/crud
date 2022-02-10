package com.whatswater.curd.project.module.workflow.flowInstanceTask;


public enum FlowInstanceTaskStatusEnum {
    INIT(1, "init"),
    CANCEL(2, "cancel"),
    COMPLETE(3, "complete"),
    REVOKE(4, "back"),
    ;

    private final Integer id;
    private final String value;

    FlowInstanceTaskStatusEnum(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static boolean isComplete(Integer status) {
        return COMPLETE.id.equals(status);
    }
}
