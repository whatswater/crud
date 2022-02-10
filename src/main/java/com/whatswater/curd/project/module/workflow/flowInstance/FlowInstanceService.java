package com.whatswater.curd.project.module.workflow.flowInstance;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkService;
import com.whatswater.curd.project.module.workflow.flowLinkRelation.FlowLinkRelationService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.stream.Collectors;

public class FlowInstanceService {
    private final FlowInstanceSQL flowInstanceSQL;

    public FlowInstanceService(MySQLPool pool) {
        this.flowInstanceSQL = new FlowInstanceSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowInstance>> search(Page page, FlowInstanceQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowInstanceSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowInstanceSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowInstance::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowInstance> getById(Long flowInstanceId) {
        Future<JsonObject> result = flowInstanceSQL.selectById(flowInstanceId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowInstance(json);
        });
    }

    public Future<Long> insert(FlowInstance flowInstance) {
        return flowInstanceSQL.insertNonEmptyGeneratedKeys(flowInstance, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowInstance flowInstance) {
        return flowInstanceSQL.updateNonEmptyById(flowInstance);
    }
}
