package com.whatswater.curd.project.module.annualTask;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class AnnualTaskSQL extends CommonSQL<AnnualTask, MySQLPool> {
    public AnnualTaskSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
