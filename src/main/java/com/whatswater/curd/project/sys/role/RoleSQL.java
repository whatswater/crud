package com.whatswater.curd.project.sys.role;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class RoleSQL extends CommonSQL<Role, MySQLPool> {
    public RoleSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
