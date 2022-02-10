package com.whatswater.curd.project.module.workflow.flowDefinition;


public enum FlowStartTypeEnum {
    BY_MANUAL(1, "手动创建");

    private final int code;
    private final String name;

    FlowStartTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
