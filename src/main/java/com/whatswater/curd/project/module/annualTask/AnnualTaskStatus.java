package com.whatswater.curd.project.module.annualTask;


public enum AnnualTaskStatus {
    INIT(1, "初始化"),
    IN_REVIEW(2, "审核中"),
    OVERRIDE(3, "已过期"),
    APPROVED(4, "已审核"),
    ;

    private Integer id;
    private String name;

    AnnualTaskStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
