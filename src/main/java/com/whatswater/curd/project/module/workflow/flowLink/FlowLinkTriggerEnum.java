package com.whatswater.curd.project.module.workflow.flowLink;

/**
 * 流程环节中事件触发的时机
 * <code>AFTER_ALL</code> 在所有任务创建完成后执行
 * <code>BEFORE_EACH</code> 每个任务创建时执行
 * <code>AFTER_EACH</code> 每个任务创建后执行
 */
public enum FlowLinkTriggerEnum {
    AFTER_ALL("event.afterAll"),
    BEFORE_EACH("event.beforeEach"),
    AFTER_EACH("event.afterEach"),
    ;

    private final String constantName;

    FlowLinkTriggerEnum(String constantName) {
        this.constantName = constantName;
    }

    public String getConstantName() {
        return constantName;
    }
}
