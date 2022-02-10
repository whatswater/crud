package com.whatswater.curd.project.module.workflow.flowLinkConstant;


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

@Table("w_flow_link_constant")
public class FlowLinkConstant {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_LINK_ID = "flow_link_id";
    public static final String COLUMN_CONSTANT_NAME = "constant_name";
    public static final String COLUMN_CONSTANT_VALUE = "constant_value";
    public static final String COLUMN_REMARK = "remark";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_LINK_ID)
    Long flowLinkId;
    @TableColumn(COLUMN_CONSTANT_NAME)
    String constantName;
    @TableColumn(COLUMN_CONSTANT_VALUE)
    String constantValue;
    @TableColumn(COLUMN_REMARK)
    String remark;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public FlowLinkConstant() {

    }

    public FlowLinkConstant(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowLinkId = json.getLong(COLUMN_FLOW_LINK_ID);
        this.constantName = json.getString(COLUMN_CONSTANT_NAME);
        this.constantValue = json.getString(COLUMN_CONSTANT_VALUE);
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

    public Long getFlowLinkId() {
        return this.flowLinkId;
    }

    public void setFlowLinkId(Long flowLinkId) {
        this.flowLinkId = flowLinkId;
    }

    public String getConstantName() {
        return this.constantName;
    }

    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }

    public String getConstantValue() {
        return this.constantValue;
    }

    public void setConstantValue(String constantValue) {
        this.constantValue = constantValue;
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

    public static SqlAssist linkIdSqlAssist(long linkId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_FLOW_LINK_ID, linkId);

        return sqlAssist;
    }
}
