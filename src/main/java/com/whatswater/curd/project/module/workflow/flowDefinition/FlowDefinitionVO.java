package com.whatswater.curd.project.module.workflow.flowDefinition;


public class FlowDefinitionVO {
    Long id;
    String title;
    String remark;
    String flowDefinitionCode;
    Integer oldVersionNo;
    Long updateTraceId;
    String graph;
    String x6Json;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFlowDefinitionCode() {
        return flowDefinitionCode;
    }

    public void setFlowDefinitionCode(String flowDefinitionCode) {
        this.flowDefinitionCode = flowDefinitionCode;
    }

    public Integer getOldVersionNo() {
        return oldVersionNo;
    }

    public void setOldVersionNo(Integer oldVersionNo) {
        this.oldVersionNo = oldVersionNo;
    }

    public Long getUpdateTraceId() {
        return updateTraceId;
    }

    public void setUpdateTraceId(Long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getX6Json() {
        return x6Json;
    }

    public void setX6Json(String x6Json) {
        this.x6Json = x6Json;
    }
}
