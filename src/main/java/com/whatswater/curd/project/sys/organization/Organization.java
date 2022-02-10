package com.whatswater.curd.project.sys.organization;

import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.util.List;

@Table("sys_organization")
public class Organization {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ORGANIZATION_CODE = "organization_code";
    public static final String COLUMN_ORGANIZATION_NAME = "organization_name";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_LEAF = "leaf";
    public static final String COLUMN_UPDATE_TRACE_ID = "update_trace_id";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_PATH)
    private String path;
    @TableColumn(COLUMN_LEVEL)
    private Integer level;
    @TableColumn(COLUMN_PARENT_ID)
    private Long parentId;
    @TableColumn(COLUMN_LEAF)
    private boolean leaf;
    @TableColumn(COLUMN_ORGANIZATION_CODE)
    private String organizationCode;
    @TableColumn(COLUMN_ORGANIZATION_NAME)
    private String organizationName;
    @TableColumn(COLUMN_UPDATE_TRACE_ID)
    private Long updateTraceId;

    public Organization() {
    }

    public Organization(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.path = json.getString(COLUMN_PATH);
        this.level = json.getInteger(COLUMN_LEVEL);
        this.parentId = json.getLong(COLUMN_PARENT_ID);
        Integer leaf = json.getInteger(COLUMN_LEAF);

        this.leaf = leaf != null && leaf.equals(1);
        this.organizationCode = json.getString(COLUMN_ORGANIZATION_CODE);
        this.organizationName = json.getString(COLUMN_ORGANIZATION_NAME);
        this.updateTraceId = json.getLong(COLUMN_UPDATE_TRACE_ID);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Long getUpdateTraceId() {
        return updateTraceId;
    }

    public void setUpdateTraceId(Long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }

    public static SqlAssist parentIdSqlAssist(long parentId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_PARENT_ID, parentId);

        return sqlAssist;
    }
    public static SqlAssist idListSqlAssist(List<Long> idList) {
        return CrudUtils.andIn(COLUMN_ID, idList);
    }

    public static SqlAssist codeOrNameLimitOneSqlAssist(String code, String name) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_ORGANIZATION_CODE, code);
        sqlAssist.andEq(COLUMN_ORGANIZATION_NAME, name);
        sqlAssist.setRowSize(1);
        return sqlAssist;
    }

    public static SqlAssist topLevelSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_LEVEL, 1);
        return sqlAssist;
    }

    public static SqlAssist codeListSqlAssist(List<String> codeList) {
        return CrudUtils.andIn(COLUMN_ORGANIZATION_CODE, codeList);
    }
}
