package com.whatswater.curd.project.sys.serial;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class SerialSQL extends CommonSQL<Serial, MySQLPool> {
    public SerialSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
