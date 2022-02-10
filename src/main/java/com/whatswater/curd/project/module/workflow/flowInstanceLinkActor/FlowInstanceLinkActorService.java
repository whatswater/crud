package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FlowInstanceLinkActorService {
    private FlowInstanceLinkActorSQL flowInstanceLinkActorSQL;

    public FlowInstanceLinkActorService() {
    }

    public Future<PageResult<FlowInstanceLinkActor>> search(Page page, FlowInstanceLinkActorQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowInstanceLinkActorSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowInstanceLinkActorSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowInstanceLinkActor::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowInstanceLinkActor> getById(Long flowInstanceLinkActorId) {
        Future<JsonObject> result = flowInstanceLinkActorSQL.selectById(flowInstanceLinkActorId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowInstanceLinkActor(json);
        });
    }

    public Future<List<FlowInstanceLinkActor>> listBy(Long prevTaskId, Long linkId) {
        SqlAssist sqlAssist = FlowInstanceLinkActor.prevTaskIdLinkIdSqlAssist(prevTaskId, linkId);
        return listBySqlAssist(sqlAssist);
    }

    public Future<List<FlowInstanceLinkActor>> listBySqlAssist(SqlAssist sqlAssist) {
        return flowInstanceLinkActorSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(FlowInstanceLinkActor::new).collect(Collectors.toList());
        });
    }

    public Future<List<Long>> batchInsert(List<FlowInstanceLinkActor> actorList) {
        LocalDateTime now = LocalDateTime.now();
        List<SameFutureBuilder<Long>> taskBuilderList = actorList.stream().map(actor -> (SameFutureBuilder<Long>)rList -> {
            actor.setCreateTime(now);
            return insert(actor);
        }).collect(Collectors.toList());

        return CrudUtils.serialTask(taskBuilderList);
    }

    public Future<Long> insert(FlowInstanceLinkActor flowInstanceLinkActor) {
        return flowInstanceLinkActorSQL.insertNonEmptyGeneratedKeys(flowInstanceLinkActor, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowInstanceLinkActor flowInstanceLinkActor) {
        return flowInstanceLinkActorSQL.updateNonEmptyById(flowInstanceLinkActor);
    }

    public void setFlowInstanceLinkActorSQL(FlowInstanceLinkActorSQL flowInstanceLinkActorSQL) {
        this.flowInstanceLinkActorSQL = flowInstanceLinkActorSQL;
    }
}
