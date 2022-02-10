package com.whatswater.curd.project.module.workflow.flowLink;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class FlowLinkSQL extends CommonSQL<FlowLink, MySQLPool> {
    public FlowLinkSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
