package com.whatswater.curd.project.module.workflow.flowDefinition;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowDefinitionQuery {
    
    String flowDefinitionCode;
    LocalDateTime createTimeStart;
    LocalDateTime createTimeEnd;
    String title;
    
    
    public String getFlowDefinitionCode() {
        return this.flowDefinitionCode;
    }
    
    public void setFlowDefinitionCode(String flowDefinitionCode) {
        this.flowDefinitionCode = flowDefinitionCode;
    }
    
    public LocalDateTime getCreateTimeStart() {
        return this.createTimeStart;
    }
    
    public void setCreateTimeStart(LocalDateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }
    
    public LocalDateTime getCreateTimeEnd() {
        return this.createTimeEnd;
    }
    
    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(flowDefinitionCode)) {
            sqlAssist.andEq(FlowDefinition.COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        }
        if (Objects.nonNull(createTimeStart)) {
            sqlAssist.andGte(FlowDefinition.COLUMN_CREATE_TIME, createTimeStart);
        }
        if (Objects.nonNull(createTimeEnd)) {
            sqlAssist.andLte(FlowDefinition.COLUMN_CREATE_TIME, createTimeEnd);
        }
        if (StrUtil.isNotEmpty(title)) {
            sqlAssist.andLike(FlowDefinition.COLUMN_TITLE, "%" + title + "%");
        }
        return sqlAssist;
    }
}
