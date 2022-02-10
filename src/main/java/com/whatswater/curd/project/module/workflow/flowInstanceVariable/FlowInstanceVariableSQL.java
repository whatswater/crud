package com.whatswater.curd.project.module.workflow.flowInstanceVariable;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceVariableSQL extends CommonSQL<FlowInstanceVariable, MySQLPool> {
    public FlowInstanceVariableSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
