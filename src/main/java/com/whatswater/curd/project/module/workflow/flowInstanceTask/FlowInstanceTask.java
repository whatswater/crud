package com.whatswater.curd.project.module.workflow.flowInstanceTask;


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

@Table("w_flow_instance_task")
public class FlowInstanceTask {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_INSTANCE_ID = "flow_instance_id";
    public static final String COLUMN_FLOW_LINK_ID = "flow_link_id";
    public static final String COLUMN_FLOW_LINK_CODE = "flow_link_code";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ACTOR = "actor";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_INSTANCE_ID)
    Long flowInstanceId;
    @TableColumn(COLUMN_FLOW_LINK_ID)
    Long flowLinkId;
    @TableColumn(COLUMN_FLOW_LINK_CODE)
    String flowLinkCode;
    @TableColumn(COLUMN_TYPE)
    Integer type;
    @TableColumn(COLUMN_STATUS)
    Integer status;
    @TableColumn(COLUMN_ACTOR)
    String actor;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public FlowInstanceTask() {

    }

    public FlowInstanceTask(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowInstanceId = json.getLong(COLUMN_FLOW_INSTANCE_ID);
        this.flowLinkId = json.getLong(COLUMN_FLOW_LINK_ID);
        this.flowLinkCode = json.getString(COLUMN_FLOW_LINK_CODE);
        this.type = json.getInteger(COLUMN_TYPE);
        this.status = json.getInteger(COLUMN_STATUS);
        this.actor = json.getString(COLUMN_ACTOR);

        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getActor() {
        return this.actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist linkIdSqlAssist(long linkId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_LINK_ID, linkId);

        return sqlAssist;
    }

    public static SqlAssist instanceIdSqlAssist(long instanceId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_INSTANCE_ID, instanceId);

        return sqlAssist;
    }

    public static SqlAssist instanceIdLinkCodeSqlAssist(long instanceId, String flowLinkCode) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_INSTANCE_ID, instanceId);
        sqlAssist.andEq(COLUMN_FLOW_LINK_CODE, flowLinkCode);

        return sqlAssist;
    }
}
