package com.whatswater.sql.executor;


import com.whatswater.sql.dialect.Dialect.SQL;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface Executor {
    Future<RowSet<Row>> query(SQL sql);
}
