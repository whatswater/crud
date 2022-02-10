package com.whatswater.curd.project.module.workflow.flowConstant;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.util.Objects;

public class FlowConstantQuery {
    
    Long flowDefinitionId;
    String flowDefinitionCode;
    String remark;
    String constantName;
    
    
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
    
    public String getRemark() {
        return this.remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getConstantName() {
        return this.constantName;
    }
    
    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowDefinitionId)) {
            sqlAssist.andEq(FlowConstant.COLUMN_FLOW_DEFINITION_ID, flowDefinitionId);
        }
        if (StrUtil.isNotEmpty(flowDefinitionCode)) {
            sqlAssist.andEq(FlowConstant.COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        }
        if (StrUtil.isNotEmpty(remark)) {
            sqlAssist.andLike(FlowConstant.COLUMN_REMARK, "%" + remark + "%");
        }
        if (StrUtil.isNotEmpty(constantName)) {
            sqlAssist.andLike(FlowConstant.COLUMN_CONSTANT_NAME, "%" + constantName + "%");
        }
        return sqlAssist;
    }
}
