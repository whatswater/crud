package com.whatswater.curd.project.module.workflow.flowInstance;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.module.workflow.flowInstanceVariable.FlowInstanceVariable;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstant;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Table("w_flow_instance")
public class FlowInstance {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_DEFINITION_ID = "flow_definition_id";
    public static final String COLUMN_FLOW_DEFINITION_CODE = "flow_definition_code";
    public static final String COLUMN_FLOW_VERSION_NO = "flow_version_no";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_START_TYPE = "start_type";
    public static final String COLUMN_START_USER = "start_user";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_DEFINITION_ID)
    Long flowDefinitionId;
    @TableColumn(COLUMN_FLOW_DEFINITION_CODE)
    String flowDefinitionCode;
    @TableColumn(COLUMN_FLOW_VERSION_NO)
    Integer flowVersionNo;
    @TableColumn(COLUMN_START_TIME)
    LocalDateTime startTime;
    @TableColumn(COLUMN_START_TYPE)
    Integer startType;
    @TableColumn(COLUMN_START_USER)
    String startUser;

    Map<String, FlowInstanceVariable> variableTable;

    public FlowInstance() {

    }

    public FlowInstance(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowDefinitionId = json.getLong(COLUMN_FLOW_DEFINITION_ID);
        this.flowDefinitionCode = json.getString(COLUMN_FLOW_DEFINITION_CODE);
        this.flowVersionNo = json.getInteger(COLUMN_FLOW_VERSION_NO);
        String startTime = json.getString(COLUMN_START_TIME);
        this.startTime = StrUtil.isEmpty(startTime) ? null : CrudUtils.parseSqlDateTimeFormat(startTime);
        this.startType = json.getInteger(COLUMN_START_TYPE);
        this.startUser = json.getString(COLUMN_START_USER);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getFlowVersionNo() {
        return this.flowVersionNo;
    }

    public void setFlowVersionNo(Integer flowVersionNo) {
        this.flowVersionNo = flowVersionNo;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getStartType() {
        return this.startType;
    }

    public void setStartType(Integer startType) {
        this.startType = startType;
    }

    public String getStartUser() {
        return this.startUser;
    }

    public void setStartUser(String startUser) {
        this.startUser = startUser;
    }

    public Map<String, FlowInstanceVariable> getVariableTable() {
        return variableTable;
    }

    public void setVariableTable(Map<String, FlowInstanceVariable> variableTable) {
        this.variableTable = variableTable;
    }

    public String getVariableValue(String variableName) {
        if (this.variableTable == null) {
            return null;
        }
        FlowInstanceVariable flowInstanceVariable = this.variableTable.get(variableName);
        if (flowInstanceVariable == null) {
            return null;
        }
        return flowInstanceVariable.getVariableValue();
    }
}
