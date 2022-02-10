package com.whatswater.curd.project.module.workflow.flowLinkRelation;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlowLinkRelationQuery {

    Long startLinkId;
    Long endLinkId;
    String routerName;
    LocalDateTime createTime;


    public Long getStartLinkId() {
        return this.startLinkId;
    }

    public void setStartLinkId(Long startLinkId) {
        this.startLinkId = startLinkId;
    }

    public Long getEndLinkId() {
        return this.endLinkId;
    }

    public void setEndLinkId(Long endLinkId) {
        this.endLinkId = endLinkId;
    }

    public String getRouterName() {
        return this.routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(startLinkId)) {
            sqlAssist.andEq(FlowLinkRelation.COLUMN_START_LINK_ID, startLinkId);
        }
        if (Objects.nonNull(endLinkId)) {
            sqlAssist.andEq(FlowLinkRelation.COLUMN_END_LINK_ID, endLinkId);
        }
        if (StrUtil.isNotEmpty(routerName)) {
            sqlAssist.andEq(FlowLinkRelation.COLUMN_ROUTE_NAME, routerName);
        }
        if (Objects.nonNull(createTime)) {
            sqlAssist.andEq(FlowLinkRelation.COLUMN_CREATE_TIME, createTime);
        }
        return sqlAssist;
    }
}
