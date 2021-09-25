package com.whatswater.sql.executor;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;


public interface QueryCallBack {
    void extractData(RowSet<Row> rows);
}
