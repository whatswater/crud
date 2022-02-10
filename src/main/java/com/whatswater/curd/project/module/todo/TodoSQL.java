package com.whatswater.curd.project.module.todo;


import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class TodoSQL extends CommonSQL<Todo, MySQLPool> {
    public TodoSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
