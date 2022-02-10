package com.whatswater.curd.project.module.workflow.flowInstanceTask;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowInstanceTaskQuery {
    
    Long flowInstanceId;
    Long flowLinkId;
    String flowLinkCode;
    String actor;
    Integer status;
    Integer type;
    LocalDateTime createTime;
    
    
    public Long getFlowInstanceId() {
        return this.flowInstanceId;
    }
    
    public void setFlowInstanceId(Long flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }
    
    public Long getFlowLinkId() {
        return this.flowLinkId;
    }
    
    public void setFlowLinkId(Long flowLinkId) {
        this.flowLinkId = flowLinkId;
    }
    
    public String getFlowLinkCode() {
        return this.flowLinkCode;
    }
    
    public void setFlowLinkCode(String flowLinkCode) {
        this.flowLinkCode = flowLinkCode;
    }
    
    public String getActor() {
        return this.actor;
    }
    
    public void setActor(String actor) {
        this.actor = actor;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowInstanceId)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_FLOW_INSTANCE_ID, flowInstanceId);
        }
        if (Objects.nonNull(flowLinkId)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_FLOW_LINK_ID, flowLinkId);
        }
        if (StrUtil.isNotEmpty(flowLinkCode)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_FLOW_LINK_CODE, flowLinkCode);
        }
        if (StrUtil.isNotEmpty(actor)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_ACTOR, actor);
        }
        if (Objects.nonNull(status)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_STATUS, status);
        }
        if (Objects.nonNull(type)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_TYPE, type);
        }
        if (Objects.nonNull(createTime)) {
            sqlAssist.andEq(FlowInstanceTask.COLUMN_CREATE_TIME, createTime);
        }
        return sqlAssist;
    }
}
