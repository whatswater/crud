package com.whatswater.curd.project.module.workflow.flowConstant;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.stream.Collectors;

public class FlowConstantService {
    private final FlowConstantSQL flowConstantSQL;

    public FlowConstantService(MySQLPool pool) {
        this.flowConstantSQL = new FlowConstantSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowConstant>> search(Page page, FlowConstantQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowConstantSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowConstantSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowConstant::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowConstant> getById(Long flowConstantId) {
        Future<JsonObject> result = flowConstantSQL.selectById(flowConstantId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowConstant(json);
        });
    }

    public Future<Long> insert(FlowConstant flowConstant) {
        return flowConstantSQL.insertNonEmptyGeneratedKeys(flowConstant, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowConstant flowConstant) {
        return flowConstantSQL.updateNonEmptyById(flowConstant);
    }
}
