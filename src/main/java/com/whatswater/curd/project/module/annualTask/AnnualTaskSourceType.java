package com.whatswater.curd.project.module.annualTask;


public enum AnnualTaskSourceType {
    INIT(1, "省公司重点工作细化目标"),
    IN_REVIEW(2, "年度目标"),
    ;

    private Integer id;
    private String name;

    AnnualTaskSourceType(Integer id, String name) {
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
