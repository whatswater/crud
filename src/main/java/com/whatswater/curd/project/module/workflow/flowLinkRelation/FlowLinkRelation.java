package com.whatswater.curd.project.module.workflow.flowLinkRelation;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Table("w_flow_link_relation")
public class FlowLinkRelation {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_START_LINK_ID = "start_link_id";
    public static final String COLUMN_END_LINK_ID = "end_link_id";
    public static final String COLUMN_ROUTE_NAME = "route_name";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_START_LINK_ID)
    Long startLinkId;
    @TableColumn(COLUMN_END_LINK_ID)
    Long endLinkId;
    @TableColumn(COLUMN_ROUTE_NAME)
    String routeName;
    @TableColumn(COLUMN_REMARK)
    String remark;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public FlowLinkRelation() {

    }

    public FlowLinkRelation(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.startLinkId = json.getLong(COLUMN_START_LINK_ID);
        this.endLinkId = json.getLong(COLUMN_END_LINK_ID);
        this.routeName = json.getString(COLUMN_ROUTE_NAME);
        this.remark = json.getString(COLUMN_REMARK);

        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRouteName() {
        return this.routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist startLinkIdSqlAssist(long startLinkId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_START_LINK_ID, startLinkId);
        return sqlAssist;
    }

    public static SqlAssist endLinkIdSqlAssist(long endLinkId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_END_LINK_ID, endLinkId);
        return sqlAssist;
    }
}
