package com.whatswater.curd.project.module.projectReward.projectRewardApply;


public enum ProjectRewardApplyStatusEnum {
    INIT(0, "草稿"),
    COMMITTED(1, "已提交"),
    APPROVED(2, "已审核"),
    ;

    private final int code;
    private final String name;

    ProjectRewardApplyStatusEnum(int code, String name) {
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
