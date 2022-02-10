package com.whatswater.curd.project.module.workflow.flowDefinition;


import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Table("w_flow_definition")
public class FlowDefinition {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_FLOW_DEFINITION_CODE = "flow_definition_code";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_VERSION_NO = "version_no";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_UPDATE_TRACE_ID = "update_trace_id";


    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_TITLE)
    String title;
    @TableColumn(COLUMN_REMARK)
    String remark;
    @TableColumn(COLUMN_FLOW_DEFINITION_CODE)
    String flowDefinitionCode;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;
    @TableColumn(COLUMN_VERSION_NO)
    Integer versionNo;
    @TableColumn(COLUMN_STATUS)
    Integer status;
    @TableColumn(COLUMN_UPDATE_TRACE_ID)
    Long updateTraceId;


    public FlowDefinition() {

    }

    public FlowDefinition(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.title = json.getString(COLUMN_TITLE);
        this.remark = json.getString(COLUMN_REMARK);
        this.flowDefinitionCode = json.getString(COLUMN_FLOW_DEFINITION_CODE);

        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = CrudUtils.parseSqlDateTimeFormat(createTime);
        this.versionNo = json.getInteger(COLUMN_VERSION_NO);
        this.status = json.getInteger(COLUMN_STATUS);
        this.updateTraceId = json.getLong(COLUMN_UPDATE_TRACE_ID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Integer getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUpdateTraceId() {
        return this.updateTraceId;
    }

    public void setUpdateTraceId(Long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }


    public static SqlAssist codeVersionSqlAssist(String flowDefinitionCode, int versionNo) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        sqlAssist.andEq(COLUMN_VERSION_NO, versionNo);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }

    public static SqlAssist codeStatusSqlAssist(String flowDefinitionCode, int status) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_DEFINITION_CODE, flowDefinitionCode);
        sqlAssist.andEq(COLUMN_STATUS, status);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}
