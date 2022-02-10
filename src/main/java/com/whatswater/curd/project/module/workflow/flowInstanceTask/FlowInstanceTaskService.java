package com.whatswater.curd.project.module.workflow.flowInstanceTask;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.ErrorCodeEnum;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FlowInstanceTaskService {
    private final FlowInstanceTaskSQL flowInstanceTaskSQL;

    public FlowInstanceTaskService(MySQLPool pool) {
        this.flowInstanceTaskSQL = new FlowInstanceTaskSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowInstanceTask>> search(Page page, FlowInstanceTaskQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowInstanceTaskSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowInstanceTaskSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowInstanceTask::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowInstanceTask> getById(Long flowInstanceTaskId) {
        Future<JsonObject> result = flowInstanceTaskSQL.selectById(flowInstanceTaskId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowInstanceTask(json);
        });
    }

    public Future<Long> insert(FlowInstanceTask flowInstanceTask) {
        return flowInstanceTaskSQL.insertNonEmptyGeneratedKeys(flowInstanceTask, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowInstanceTask flowInstanceTask) {
        return flowInstanceTaskSQL.updateNonEmptyById(flowInstanceTask);
    }

    public Future<FlowInstanceTask> explicitGetOneByLinkCode(long flowInstanceId, String linkCode) {
        SqlAssist sqlAssist = FlowInstanceTask.instanceIdLinkCodeSqlAssist(flowInstanceId, linkCode);
        return flowInstanceTaskSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                throw ErrorCodeEnum.GET_NOT_EXISTS.toException();
            }
            if (jsonList.size() > 1) {
                throw ErrorCodeEnum.TOO_MANY_RESULT.toException();
            }
            return new FlowInstanceTask(jsonList.get(0));
        });
    }

    /**
     * 查询环节的所有任务
     * @param linkId 流程环节Id
     * @return 环节任务状态列表
     */
    public Future<List<FlowInstanceTask>> queryFlowInstanceTask(long linkId) {
        SqlAssist sqlAssist = FlowInstanceTask.linkIdSqlAssist(linkId);
        return flowInstanceTaskSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            return list.stream().map(FlowInstanceTask::new).collect(Collectors.toList());
        });
    }

    /**
     * 查询流程实例的所有任务
     * @param instanceId 流程实例Id
     * @return 流程任务列表
     */
    public Future<List<FlowInstanceTask>> queryByInstanceId(long instanceId) {
        SqlAssist sqlAssist = FlowInstanceTask.instanceIdSqlAssist(instanceId);
        return flowInstanceTaskSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            return list.stream().map(FlowInstanceTask::new).collect(Collectors.toList());
        });
    }

    /**
     * 更新任务状态
     * @param taskId 任务Id
     * @param statusEnum 状态
     * @return 更新计数
     */
    public Future<Integer> updateTaskStatus(final long taskId, FlowInstanceTaskStatusEnum statusEnum) {
        FlowInstanceTask flowInstanceTask = new FlowInstanceTask();
        flowInstanceTask.setId(taskId);
        flowInstanceTask.setStatus(statusEnum.getId());
        return update(flowInstanceTask);
    }
}
