package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

@Table("w_flow_instance_task_relation")
public class FlowInstanceTaskRelation {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_INSTANCE_ID = "flow_instance_id";
    public static final String COLUMN_PREV_TASK_ID = "prev_task_id";
    public static final String COLUMN_NEXT_TASK_ID = "next_task_id";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_INSTANCE_ID)
    Long flowInstanceId;
    @TableColumn(COLUMN_PREV_TASK_ID)
    Long prevTaskId;
    @TableColumn(COLUMN_NEXT_TASK_ID)
    Long nextTaskId;

    public FlowInstanceTaskRelation() {

    }

    public FlowInstanceTaskRelation(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowInstanceId = json.getLong(COLUMN_FLOW_INSTANCE_ID);
        this.prevTaskId = json.getLong(COLUMN_PREV_TASK_ID);
        this.nextTaskId = json.getLong(COLUMN_NEXT_TASK_ID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlowInstanceId() {
        return flowInstanceId;
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

    public Long getNextTaskId() {
        return this.nextTaskId;
    }

    public void setNextTaskId(Long nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public static SqlAssist nextTaskIdSqlAssist(Long nextTaskId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_NEXT_TASK_ID, nextTaskId);

        return sqlAssist;
    }

    public static SqlAssist flowInstanceIdSqlAssist(Long flowInstanceId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_INSTANCE_ID, flowInstanceId);

        return sqlAssist;
    }
}
