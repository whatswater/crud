package com.whatswater.curd.project.module.workflow.flowInstance;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowInstanceQuery {
    
    Long flowDefinitionId;
    String flowDefinitionCode;
    LocalDateTime startTime;
    Integer startType;
    String startUser;
    
    
    public Long getFlowDefinitionId() {
        return this.flowDefinitionId;
    }
    
    public void setFlowDefinitionId(Long flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }
    
    public String getFlowDefinitionCode() {
        return this.flowDefinitionCode;
    }
    
    public void setFlowDefinitionCode(String flowDefinitionCode) {
        this.flowDefinitionCode = flowDefinitionCode;
    }
    
    public LocalDateTime getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public Integer getStartType() {
        return this.startType;
    }
    
    public void setStartType(Integer startType) {
        this.startType = startType;
    }
    
    public String getStartUser() {
        return this.startUser;
    }
    
    public void setStartUser(String startUser) {
        this.startUser = startUser;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowDefinitionId)) {
            sqlAssist.andEq(FlowInstance.COLUMN_FLOW_DEFINITION_ID, flowDefinitionId);
        }
        if (StrUtil.isNotEmpty(flowDefinitionCode)) {
            sqlAssist.andEq(FlowInstance.COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        }
        if (Objects.nonNull(startTime)) {
            sqlAssist.andEq(FlowInstance.COLUMN_START_TIME, startTime);
        }
        if (Objects.nonNull(startType)) {
            sqlAssist.andEq(FlowInstance.COLUMN_START_TYPE, startType);
        }
        if (StrUtil.isNotEmpty(startUser)) {
            sqlAssist.andEq(FlowInstance.COLUMN_START_USER, startUser);
        }
        return sqlAssist;
    }
}
