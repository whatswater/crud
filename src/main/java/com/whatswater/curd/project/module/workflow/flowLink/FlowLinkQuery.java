package com.whatswater.curd.project.module.workflow.flowLink;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowLinkQuery {
    
    Long flowDefinitionId;
    String flowLinkCode;
    String flowDefinitionCode;
    LocalDateTime createTime;
    String title;
    
    
    public Long getFlowDefinitionId() {
        return this.flowDefinitionId;
    }
    
    public void setFlowDefinitionId(Long flowDefinitionId) {
        this.flowDefinitionId = flowDefinitionId;
    }
    
    public String getFlowLinkCode() {
        return this.flowLinkCode;
    }
    
    public void setFlowLinkCode(String flowLinkCode) {
        this.flowLinkCode = flowLinkCode;
    }
    
    public String getFlowDefinitionCode() {
        return this.flowDefinitionCode;
    }
    
    public void setFlowDefinitionCode(String flowDefinitionCode) {
        this.flowDefinitionCode = flowDefinitionCode;
    }
    
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowDefinitionId)) {
            sqlAssist.andEq(FlowLink.COLUMN_FLOW_DEFINITION_ID, flowDefinitionId);
        }
        if (StrUtil.isNotEmpty(flowLinkCode)) {
            sqlAssist.andEq(FlowLink.COLUMN_FLOW_LINK_CODE, flowLinkCode);
        }
        if (StrUtil.isNotEmpty(flowDefinitionCode)) {
            sqlAssist.andEq(FlowLink.COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        }
        if (Objects.nonNull(createTime)) {
            sqlAssist.andEq(FlowLink.COLUMN_CREATE_TIME, createTime);
        }
        if (StrUtil.isNotEmpty(title)) {
            sqlAssist.andLike(FlowLink.COLUMN_TITLE, "%" + title + "%");
        }
        return sqlAssist;
    }
}
