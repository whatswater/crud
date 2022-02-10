package com.whatswater.curd.project.module.workflow.flowLinkRelation;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkRelationSQL extends CommonSQL<FlowLinkRelation, MySQLPool> {
    public FlowLinkRelationSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
