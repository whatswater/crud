package com.whatswater.curd.project.module.workflow.flowInstanceVariable;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowInstanceVariableQuery {
    
    String variableName;
    Long flowInstanceId;
    LocalDateTime createTime;
    
    
    public String getVariableName() {
        return this.variableName;
    }
    
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }
    
    public Long getFlowInstanceId() {
        return this.flowInstanceId;
    }
    
    public void setFlowInstanceId(Long flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }
    
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(variableName)) {
            sqlAssist.andEq(FlowInstanceVariable.COLUMN_VARIABLE_NAME, variableName);
        }
        if (Objects.nonNull(flowInstanceId)) {
            sqlAssist.andEq(FlowInstanceVariable.COLUMN_FLOW_INSTANCE_ID, flowInstanceId);
        }
        if (Objects.nonNull(createTime)) {
            sqlAssist.andEq(FlowInstanceVariable.COLUMN_CREATE_TIME, createTime);
        }
        return sqlAssist;
    }
}
