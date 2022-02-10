package com.whatswater.curd.project.sys.employeeRole;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeRoleSQL extends CommonSQL<EmployeeRole, MySQLPool> {
    public EmployeeRoleSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
