package com.whatswater.curd.project.module.todo;


public enum TodoStatusEnum {
    UNREAD(1, "未读"),
    READ(2, "已读"),
    CANCEL(3, "取消"),
    EXECUTING(4, "执行中"),
    COMPLETE(5, "完成"),
    ;

    private Integer code;
    private String name;

    TodoStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
