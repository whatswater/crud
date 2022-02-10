package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceTaskRelationSQL extends CommonSQL<FlowInstanceTaskRelation, MySQLPool> {
    public FlowInstanceTaskRelationSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
