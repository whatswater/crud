package com.whatswater.curd.project.sys.employeeRole;


import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.util.List;

@Table("sys_employee_role")
public class EmployeeRole {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ROLE_ID = "role_id";
    public static final String COLUMN_USER_ID = "user_id";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_ROLE_ID)
    Long roleId;
    @TableColumn(COLUMN_USER_ID)
    Long userId;

    public EmployeeRole() {

    }

    public EmployeeRole(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.roleId = json.getLong(COLUMN_ROLE_ID);
        this.userId = json.getLong(COLUMN_USER_ID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static SqlAssist roleIdSqlAssist(long roleId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_ROLE_ID, roleId);

        return sqlAssist;
    }

    public static SqlAssist roleIdListSqlAssist(List<Long> roleIdList) {
        return CrudUtils.andIn(COLUMN_ROLE_ID, roleIdList);
    }

    public static SqlAssist useIdSqlAssist(long userId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_USER_ID, userId);

        return sqlAssist;
    }
}
