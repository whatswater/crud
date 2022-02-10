package com.whatswater.curd.project.module.workflow.flowLinkConstant;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkConstantSQL extends CommonSQL<FlowLinkConstant, MySQLPool> {
    public FlowLinkConstantSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
