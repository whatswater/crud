package com.whatswater.curd.project.module.workflow.flowInstanceVariable;


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

@Table("w_flow_instance_variable")
public class FlowInstanceVariable {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_INSTANCE_ID = "flow_instance_id";
    public static final String COLUMN_VARIABLE_NAME = "variable_name";
    public static final String COLUMN_VARIABLE_VALUE = "variable_value";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_INSTANCE_ID)
    Long flowInstanceId;
    @TableColumn(COLUMN_VARIABLE_NAME)
    String variableName;
    @TableColumn(COLUMN_VARIABLE_VALUE)
    String variableValue;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public FlowInstanceVariable() {

    }

    public FlowInstanceVariable(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowInstanceId = json.getLong(COLUMN_FLOW_INSTANCE_ID);
        this.variableName = json.getString(COLUMN_VARIABLE_NAME);
        this.variableValue = json.getString(COLUMN_VARIABLE_VALUE);

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

    public String getVariableName() {
        return this.variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableValue() {
        return this.variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist instanceIdSqlAssist(long instanceId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_INSTANCE_ID, instanceId);

        return sqlAssist;
    }

    public static SqlAssist instanceIdVariableNameSqlAssist(long instanceId, String variableName) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_INSTANCE_ID, instanceId);
        sqlAssist.andEq(COLUMN_VARIABLE_NAME, variableName);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}
