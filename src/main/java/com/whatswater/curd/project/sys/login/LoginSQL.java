package com.whatswater.curd.project.sys.login;


import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class LoginSQL extends CommonSQL<LoginLog, MySQLPool> {
    public LoginSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
