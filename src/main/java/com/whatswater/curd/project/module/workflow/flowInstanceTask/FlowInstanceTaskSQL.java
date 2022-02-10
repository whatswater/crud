package com.whatswater.curd.project.module.workflow.flowInstanceTask;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceTaskSQL extends CommonSQL<FlowInstanceTask, MySQLPool> {
    public FlowInstanceTaskSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
