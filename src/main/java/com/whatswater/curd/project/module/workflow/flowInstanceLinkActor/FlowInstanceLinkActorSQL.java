package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowInstanceLinkActorSQL extends CommonSQL<FlowInstanceLinkActor, MySQLPool> {
    public FlowInstanceLinkActorSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
