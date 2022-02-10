package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;


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

@Table("w_flow_instance_link_actor")
public class FlowInstanceLinkActor {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_INSTANCE_ID = "flow_instance_id";
    public static final String COLUMN_PREV_TASK_ID = "prev_task_id";
    public static final String COLUMN_FLOW_LINK_ID = "flow_link_id";
    public static final String COLUMN_FLOW_LINK_CODE = "flow_link_code";
    public static final String COLUMN_ACTOR = "actor";
    public static final String COLUMN_ACTOR_NAME = "actor_name";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_INSTANCE_ID)
    Long flowInstanceId;
    @TableColumn(COLUMN_PREV_TASK_ID)
    Long prevTaskId;
    @TableColumn(COLUMN_FLOW_LINK_ID)
    Long flowLinkId;
    @TableColumn(COLUMN_FLOW_LINK_CODE)
    String flowLinkCode;
    @TableColumn(COLUMN_ACTOR)
    String actor;
    @TableColumn(COLUMN_ACTOR_NAME)
    String actorName;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public FlowInstanceLinkActor() {

    }

    public FlowInstanceLinkActor(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowInstanceId = json.getLong(COLUMN_FLOW_INSTANCE_ID);
        this.prevTaskId = json.getLong(COLUMN_PREV_TASK_ID);
        this.flowLinkId = json.getLong(COLUMN_FLOW_LINK_ID);
        this.flowLinkCode = json.getString(COLUMN_FLOW_LINK_CODE);
        this.actor = json.getString(COLUMN_ACTOR);
        this.actorName = json.getString(COLUMN_ACTOR_NAME);
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

    public Long getPrevTaskId() {
        return this.prevTaskId;
    }

    public void setPrevTaskId(Long prevTaskId) {
        this.prevTaskId = prevTaskId;
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

    public String getActorName() {
        return this.actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist prevTaskIdLinkIdSqlAssist(Long prevTaskId, Long linkId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_PREV_TASK_ID, prevTaskId);
        sqlAssist.andEq(COLUMN_FLOW_LINK_ID, linkId);

        return sqlAssist;
    }
}
