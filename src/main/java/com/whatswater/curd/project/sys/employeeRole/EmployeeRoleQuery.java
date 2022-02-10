package com.whatswater.curd.project.sys.employeeRole;


import io.vertx.ext.sql.assist.SqlAssist;

public class EmployeeRoleQuery {
    Long roleId;
    Long userId;

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (roleId != null) {
            sqlAssist.andEq(EmployeeRole.COLUMN_ROLE_ID, roleId);
        }
        if (userId != null) {
            sqlAssist.andEq(EmployeeRole.COLUMN_USER_ID, userId);
        }

        return sqlAssist;
    }
}
