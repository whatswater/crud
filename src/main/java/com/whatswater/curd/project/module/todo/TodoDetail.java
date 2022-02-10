package com.whatswater.curd.project.module.todo;


import cn.hutool.core.bean.BeanUtil;

public class TodoDetail extends Todo {
    private String linkCode;
    private Long flowInstanceId;

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public Long getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(Long flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public static TodoDetail fromTodo(Todo todo) {
        return BeanUtil.copyProperties(todo, TodoDetail.class);
    }
}
