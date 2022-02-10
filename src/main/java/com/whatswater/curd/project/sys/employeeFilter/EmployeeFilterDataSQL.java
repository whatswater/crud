package com.whatswater.curd.project.sys.employeeFilter;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeFilterDataSQL extends CommonSQL<EmployeeFilterData, MySQLPool> {
    public EmployeeFilterDataSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
