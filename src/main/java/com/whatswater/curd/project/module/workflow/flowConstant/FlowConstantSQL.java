package com.whatswater.curd.project.module.workflow.flowConstant;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowConstantSQL extends CommonSQL<FlowConstant, MySQLPool> {
    public FlowConstantSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
