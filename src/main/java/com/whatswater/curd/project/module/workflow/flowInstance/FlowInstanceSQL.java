package com.whatswater.curd.project.module.workflow.flowInstance;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceSQL extends CommonSQL<FlowInstance, MySQLPool> {
    public FlowInstanceSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
