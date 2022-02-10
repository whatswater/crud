package com.whatswater.curd.project.sys.menu;


import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class MenuSQL extends CommonSQL<Menu, MySQLPool> {
    public MenuSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
