package com.whatswater.curd.project.module.opinion;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class OpinionSQL extends CommonSQL<Opinion, MySQLPool> {
    public OpinionSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
