package com.whatswater.curd.datasource;


import com.whatswater.sql.executor.Executor;
import com.whatswater.sql.executor.QueryCallBack;
import io.vertx.core.Future;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.desc.ColumnDescriptor;

import java.util.List;

public class VertxExecutor implements Executor {
    private MySQLPool pool;

    public VertxExecutor(MySQLPool pool) {
        this.pool = pool;
    }

    @Override
    public void query(String sql, Object[] params, QueryCallBack queryCallBack) {
        Future<RowSet<Row>> future = pool.preparedQuery(sql).execute(Tuple.from(params));
        future.onComplete(result -> {
            if (result.succeeded()) {
                RowSet<Row> rows = result.result();
                queryCallBack.extractData(rows);
            } else {
                // 如何做错误处理
                Throwable throwable = result.cause();
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void update(String sql, Object[] params) {
//        pool.withTransaction(connection -> {
//
//        });
    }

    @Override
    public void batchUpdate(String sql, List<Object[]> batchParams) {

    }
}
