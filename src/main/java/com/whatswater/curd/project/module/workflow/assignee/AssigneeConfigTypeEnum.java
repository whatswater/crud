package com.whatswater.curd.project.module.workflow.assignee;

/**
 * 参与者类型
 * <code>NONE</code> 无参与者
 * <code>FILTER_SCRIPT</code> 过滤用户脚本
 * <code>FLOW_VARIABLE</code> 流程变量
 * <code>ASSIGNEE</code> 指定人员
 * <code>HTTP</code> http接口调用
 * <code>USER_SELECT</code> 用户提交时选择
 */
public enum AssigneeConfigTypeEnum {
    NONE("none"),
    FILTER_SCRIPT("filter_script"),
    FLOW_VARIABLE("flow_variable"),
    ASSIGNEE("assignee"),
    HTTP("http"),
    USER_SELECT("user_select"),
    ;

    private final String type;
    AssigneeConfigTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static AssigneeConfigTypeEnum getByValue(String type) {
        for (AssigneeConfigTypeEnum typeEnum: AssigneeConfigTypeEnum.values()) {
            if(typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
