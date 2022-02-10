package com.whatswater.curd.project.sys.attachment;

import io.vertx.ext.sql.assist.CommonSQL;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.mysqlclient.MySQLPool;

public class AttachmentSQL extends CommonSQL<Attachment, MySQLPool> {
    public AttachmentSQL(SQLExecute<MySQLPool> execute) {
        super(execute);
    }
}
