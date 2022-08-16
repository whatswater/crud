package com.whatswater.curd.project.sys.employee;


import com.whatswater.sql.executor.ContextService;
import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class EmployeeSQL extends CommonSQL<Employee, MySQLPool> implements ContextService<EmployeeSQL> {
    public EmployeeSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
