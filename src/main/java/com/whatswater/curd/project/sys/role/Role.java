package com.whatswater.curd.project.sys.role;

import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.util.List;

@Table("sys_role")
public class Role {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_ROLE_NAME = "role_name";
    public static final String COLUMN_UPDATE_TRACE_ID = "update_trace_id";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_CODE)
    private String code;
    @TableColumn(COLUMN_ROLE_NAME)
    private String roleName;
    @TableColumn(COLUMN_UPDATE_TRACE_ID)
    private Long updateTraceId;

    public Role() {
    }

    public Role(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.code = json.getString(COLUMN_CODE);
        this.roleName = json.getString(COLUMN_ROLE_NAME);
        this.updateTraceId = json.getLong(COLUMN_UPDATE_TRACE_ID);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getUpdateTraceId() {
        return updateTraceId;
    }

    public void setUpdateTraceId(Long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }

    public static SqlAssist codeSqlAssist(String code) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_CODE, code);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }


    public static SqlAssist nameSqlAssist(String name) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_ROLE_NAME, name);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }

    public static SqlAssist codeOrNameSqlAssist(String code, String name) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_ROLE_NAME, name);
        sqlAssist.orEq(COLUMN_CODE, code);
        return sqlAssist;
    }

    public static SqlAssist idListSqlAssist(List<Long> idList) {
        return CrudUtils.andIn(COLUMN_ID, idList);
    }

    public static SqlAssist codeListSqlAssist(List<String> codeList) {
        return CrudUtils.andIn(COLUMN_CODE, codeList);
    }
}
