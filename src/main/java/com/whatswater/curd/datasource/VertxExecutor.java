package com.whatswater.curd.datasource;


import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.executor.Executor;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class VertxExecutor implements Executor {
    private MySQLPool pool;

    public VertxExecutor(MySQLPool pool) {
        this.pool = pool;
    }

    @Override
    public Future<RowSet<Row>> query(SQL sql) {
        return pool
            .preparedQuery(sql.getSqlValue())
            .execute(Tuple.tuple(sql.getParams()));
    }
}
