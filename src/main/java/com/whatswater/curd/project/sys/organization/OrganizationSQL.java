package com.whatswater.curd.project.sys.organization;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class OrganizationSQL extends CommonSQL<Organization, MySQLPool> {
    public OrganizationSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
