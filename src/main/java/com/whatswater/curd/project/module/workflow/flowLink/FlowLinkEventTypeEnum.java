package com.whatswater.curd.project.module.workflow.flowLink;

/**
 * 流程环节的事件类型
 * <code>HTTP</code> 远程http调用
 * <code>SCRIPT</code> 执行脚本
 * <code>ENTITY_CLASS</code> 实现了接口的实体类
 * <code>DI_OBJECT</code> 通过DI获取的对象
 */
public enum FlowLinkEventTypeEnum {
    HTTP,
    SCRIPT,
    ENTITY_CLASS,
    DI_OBJECT,
    ;

    public static FlowLinkEventTypeEnum getByName(String name) {
        for (FlowLinkEventTypeEnum typeEnum: FlowLinkEventTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }

        return null;
    }
}
