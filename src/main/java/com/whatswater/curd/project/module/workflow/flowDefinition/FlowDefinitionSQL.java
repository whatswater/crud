package com.whatswater.curd.project.module.workflow.flowDefinition;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowDefinitionSQL extends CommonSQL<FlowDefinition, MySQLPool> {
    public FlowDefinitionSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
