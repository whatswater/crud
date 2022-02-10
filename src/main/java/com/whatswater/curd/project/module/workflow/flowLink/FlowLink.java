package com.whatswater.curd.project.module.workflow.flowLink;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstant;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Table("w_flow_link")
public class FlowLink {
    public static final String LINK_CODE_START = "start";
    public static final String LINK_CODE_END = "end";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_DEFINITION_ID = "flow_definition_id";
    public static final String COLUMN_FLOW_DEFINITION_CODE = "flow_definition_code";
    public static final String COLUMN_FLOW_LINK_CODE = "flow_link_code";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_DEFINITION_ID)
    Long flowDefinitionId;
    @TableColumn(COLUMN_FLOW_DEFINITION_CODE)
    String flowDefinitionCode;
    @TableColumn(COLUMN_FLOW_LINK_CODE)
    String flowLinkCode;
    @TableColumn(COLUMN_TITLE)
    String title;
    @TableColumn(COLUMN_TYPE)
    String type;
    @TableColumn(COLUMN_REMARK)
    String remark;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;

    public FlowLink() {

    }

    public FlowLink(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowDefinitionId = json.getLong(COLUMN_FLOW_DEFINITION_ID);
        this.flowDefinitionCode = json.getString(COLUMN_FLOW_DEFINITION_CODE);
        this.flowLinkCode = json.getString(COLUMN_FLOW_LINK_CODE);
        this.title = json.getString(COLUMN_TITLE);
        this.type = json.getString(COLUMN_TYPE);
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

    public String getFlowLinkCode() {
        return this.flowLinkCode;
    }

    public void setFlowLinkCode(String flowLinkCode) {
        this.flowLinkCode = flowLinkCode;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
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


    public static SqlAssist definitionIdLinkCodeSqlAssist(long flowDefinitionId, String flowLinkCode) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_DEFINITION_ID, flowDefinitionId);
        sqlAssist.andEq(COLUMN_FLOW_LINK_CODE, flowLinkCode);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }

    public static SqlAssist idListSqlAssist(List<Long> idList) {
        return CrudUtils.andIn(COLUMN_ID, idList);
    }
}
