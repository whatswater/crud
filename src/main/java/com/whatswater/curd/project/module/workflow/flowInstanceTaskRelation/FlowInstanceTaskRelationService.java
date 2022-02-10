package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import javafx.scene.control.Tab;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FlowInstanceTaskRelationService {
    private final FlowInstanceTaskRelationSQL flowInstanceTaskRelationSQL;

    public FlowInstanceTaskRelationService(MySQLPool pool) {
        this.flowInstanceTaskRelationSQL = new FlowInstanceTaskRelationSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowInstanceTaskRelation>> search(Page page, FlowInstanceTaskRelationQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowInstanceTaskRelationSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowInstanceTaskRelationSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowInstanceTaskRelation::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowInstanceTaskRelation> getById(Long flowInstanceTaskRelationId) {
        Future<JsonObject> result = flowInstanceTaskRelationSQL.selectById(flowInstanceTaskRelationId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowInstanceTaskRelation(json);
        });
    }

    public Future<List<FlowInstanceTaskRelation>> listBy(Long flowInstanceId) {
        SqlAssist sqlAssist = FlowInstanceTaskRelation.flowInstanceIdSqlAssist(flowInstanceId);
        return flowInstanceTaskRelationSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(FlowInstanceTaskRelation::new).collect(Collectors.toList());
        });
    }

    public Future<FlowInstanceTaskRelation> getByNextTaskId(Long nextTaskId) {
        SqlAssist sqlAssist = FlowInstanceTaskRelation.nextTaskIdSqlAssist(nextTaskId);
        return flowInstanceTaskRelationSQL.selectAll(sqlAssist).map(FlowInstanceTaskRelationService::mapOne);
    }

    public Future<List<Long>> batchInsert(Long prevTaskId, List<FlowInstanceTask> taskList) {
        List<SameFutureBuilder<Long>> taskBuilderList = taskList.stream().map(task -> {
            FlowInstanceTaskRelation relation = new FlowInstanceTaskRelation();
            relation.setPrevTaskId(prevTaskId);
            relation.setNextTaskId(task.getId());
            relation.setFlowInstanceId(task.getFlowInstanceId());
            return relation;
        }).map(relation -> (SameFutureBuilder<Long>)r -> {
            return this.insert(relation);
        }).collect(Collectors.toList());

        return CrudUtils.serialTask(taskBuilderList);
    }

    public Future<Long> insert(FlowInstanceTaskRelation flowInstanceTaskRelation) {
        return flowInstanceTaskRelationSQL.insertNonEmptyGeneratedKeys(flowInstanceTaskRelation, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowInstanceTaskRelation flowInstanceTaskRelation) {
        return flowInstanceTaskRelationSQL.updateNonEmptyById(flowInstanceTaskRelation);
    }

    private static FlowInstanceTaskRelation mapOne(List<JsonObject> jsonList) {
        if (CollectionUtil.isEmpty(jsonList)) {
            return null;
        }

        return new FlowInstanceTaskRelation(jsonList.get(0));
    }
}
