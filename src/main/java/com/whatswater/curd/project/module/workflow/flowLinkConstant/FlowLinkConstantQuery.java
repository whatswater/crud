package com.whatswater.curd.project.module.workflow.flowLinkConstant;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowLinkConstantQuery {
    
    Long flowLinkId;
    LocalDateTime createTime;
    String remark;
    String constantName;
    
    
    public Long getFlowLinkId() {
        return this.flowLinkId;
    }
    
    public void setFlowLinkId(Long flowLinkId) {
        this.flowLinkId = flowLinkId;
    }
    
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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
        if (Objects.nonNull(flowLinkId)) {
            sqlAssist.andEq(FlowLinkConstant.COLUMN_FLOW_LINK_ID, flowLinkId);
        }
        if (Objects.nonNull(createTime)) {
            sqlAssist.andEq(FlowLinkConstant.COLUMN_CREATE_TIME, createTime);
        }
        if (StrUtil.isNotEmpty(remark)) {
            sqlAssist.andLike(FlowLinkConstant.COLUMN_REMARK, "%" + remark + "%");
        }
        if (StrUtil.isNotEmpty(constantName)) {
            sqlAssist.andLike(FlowLinkConstant.COLUMN_CONSTANT_NAME, "%" + constantName + "%");
        }
        return sqlAssist;
    }
}
