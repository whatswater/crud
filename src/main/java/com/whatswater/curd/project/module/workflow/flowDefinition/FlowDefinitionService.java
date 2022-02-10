package com.whatswater.curd.project.module.workflow.flowDefinition;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowDefinitionService {
    private final FlowDefinitionSQL flowDefinitionSQL;
    private final FlowDefinitionGraphSQL flowDefinitionGraphSQL;

    public FlowDefinitionService(MySQLPool pool) {
        this.flowDefinitionSQL = new FlowDefinitionSQL(SQLExecute.createMySQL(pool));
        this.flowDefinitionGraphSQL = new FlowDefinitionGraphSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowDefinition>> search(Page page, FlowDefinitionQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowDefinitionSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowDefinitionSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowDefinition::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowDefinition> getById(Long flowDefinitionId) {
        Future<JsonObject> result = flowDefinitionSQL.selectById(flowDefinitionId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowDefinition(json);
        });
    }


    /**
     * 新增一个流程
     * @param flowDefinitionVo 流程图
     * @return 新增的流程Id
     */
    public Future<Long> insertVo(FlowDefinitionVO flowDefinitionVo) {
        final String json = flowDefinitionVo.getGraph();
        FlowDefinition flowDefinition = new FlowDefinition();
        flowDefinition.setFlowDefinitionCode(flowDefinitionVo.getFlowDefinitionCode());
        flowDefinition.setTitle(flowDefinitionVo.getTitle());
        flowDefinition.setRemark(flowDefinitionVo.getRemark());

        flowDefinition.setStatus(FlowDefinitionStatusEnum.INIT.getCode());
        flowDefinition.setCreateTime(LocalDateTime.now());
        if (flowDefinitionVo.getOldVersionNo() != null && flowDefinitionVo.getOldVersionNo() > 0) {
            flowDefinition.setVersionNo(flowDefinitionVo.getOldVersionNo() + 1);
            return this.insertWithUniqueCheck(flowDefinition)
                .compose(flowDefinitionId -> this.insertJson(flowDefinitionId, json, flowDefinitionVo.getX6Json()).map(flowDefinitionId))
                .compose(flowDefinitionId -> {
                    return getByCode(flowDefinitionVo.getFlowDefinitionCode(), flowDefinitionVo.getOldVersionNo())
                        .compose(this::disableOrDraftFlowDefinition)
                        .map(flowDefinitionId);
                });
        } else {
            flowDefinition.setVersionNo(1);
            return this.insertWithUniqueCheck(flowDefinition)
                .compose(flowDefinitionId -> this.insertJson(flowDefinitionId, json, flowDefinitionVo.getX6Json()).map(flowDefinitionId));
        }
    }

    public Future<Long> insertWithUniqueCheck(FlowDefinition flowDefinition) {
        return getByCode(flowDefinition.getFlowDefinitionCode(), flowDefinition.getVersionNo())
            .compose(exists -> {
                if (exists != null) {
                    return Future.failedFuture("系统中已存在流程：" + flowDefinition.getFlowDefinitionCode() + "，版本：" + flowDefinition.getVersionNo() + "");
                }
                return flowDefinitionSQL.insertNonEmptyGeneratedKeys(flowDefinition, MySQLClient.LAST_INSERTED_ID);
            });
    }

    /**
     * 禁用或者草稿化
     * @param flowDefinitionId 流程定义Id
     * @return cnt
     */
    public Future<Integer> disableOrDraftFlowDefinition(long flowDefinitionId) {
        return getById(flowDefinitionId).compose(this::disableOrDraftFlowDefinition);
    }

    public Future<Integer> disableOrDraftFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null) {
            return Future.succeededFuture(0);
        }
        if (FlowDefinitionStatusEnum.DEPLOY.getCode().equals(flowDefinition.getStatus())) {
            return updateStatus(flowDefinition.getId(), FlowDefinitionStatusEnum.DISABLED);
        } else if (FlowDefinitionStatusEnum.INIT.getCode().equals(flowDefinition.getStatus())) {
            return updateStatus(flowDefinition.getId(), FlowDefinitionStatusEnum.DRAFT);
        }
        return Future.succeededFuture(0);
    }

    public Future<Integer> enableOrInitFlowDefinition(long flowDefinitionId) {
        return getById(flowDefinitionId).compose(this::enableOrInitFlowDefinition);
    }

    public Future<Integer> enableOrInitFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null) {
            return Future.succeededFuture(0);
        }
        if (FlowDefinitionStatusEnum.DRAFT.getCode().equals(flowDefinition.getStatus())) {
            return getByCodeAndStatus(flowDefinition.getFlowDefinitionCode(), FlowDefinitionStatusEnum.INIT.getCode()).compose(db -> {
                if (db != null) {
                    return Future.failedFuture("当前已经存在初始化状态的流程");
                }
                return updateStatus(flowDefinition.getId(), FlowDefinitionStatusEnum.INIT);
            });
        } else if (FlowDefinitionStatusEnum.DISABLED.getCode().equals(flowDefinition.getStatus())) {
            return getByCodeAndStatus(flowDefinition.getFlowDefinitionCode(), FlowDefinitionStatusEnum.DEPLOY.getCode()).compose(db -> {
                if (db != null) {
                    return Future.failedFuture("当前已经存在部署状态的流程");
                }
                return updateStatus(flowDefinition.getId(), FlowDefinitionStatusEnum.DEPLOY);
            });
        }
        return Future.succeededFuture(0);
    }

    public Future<Integer> updateStatus(Long flowDefinitionId, FlowDefinitionStatusEnum status) {
        FlowDefinition update = new FlowDefinition();
        update.setId(flowDefinitionId);
        update.setStatus(status.getCode());

        return flowDefinitionSQL.updateNonEmptyById(update);
    }

    public Future<Integer> update(FlowDefinition flowDefinition) {
        return flowDefinitionSQL.updateNonEmptyById(flowDefinition);
    }

    public Future<Integer> updateVo(final FlowDefinitionVO flowDefinitionVo) {
        return getById(flowDefinitionVo.getId()).compose(flowDefinition -> {
            if (flowDefinition == null) {
                return Future.failedFuture("更新流程时根据流程Id无法查询出流程，Id：" + flowDefinitionVo.getId());
            }
            if (!FlowDefinitionStatusEnum.INIT.getCode().equals(flowDefinition.getStatus())) {
                FlowDefinitionStatusEnum status = FlowDefinitionStatusEnum.getByCode(flowDefinition.getStatus());
                return Future.failedFuture("更新流程时流程状态应该为初始化，当前状态：" + (status == null ? "未知" : status.getName()));
            }

            FlowDefinition update = new FlowDefinition();
            update.setId(flowDefinitionVo.getId());
            update.setTitle(flowDefinitionVo.getTitle());
            update.setRemark(flowDefinitionVo.getRemark());

            return update(update).compose(cnt -> this.updateGraph(flowDefinitionVo.getId(), flowDefinitionVo.getGraph()));
        });
    }

    /**
     * 根据流程编码和版本号查询流程定义
     * @param flowDefinitionCode 流程编码
     * @param versionNo 版本号
     * @return 流程定义
     */
    public Future<FlowDefinition> getByCode(String flowDefinitionCode, int versionNo) {
        SqlAssist sqlAssist = FlowDefinition.codeVersionSqlAssist(flowDefinitionCode, versionNo);
        return flowDefinitionSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowDefinition(list.get(0));
        });
    }

    public Future<FlowDefinition> getByCode(String flowDefinitionCode) {
        SqlAssist sqlAssist = FlowDefinition.codeStatusSqlAssist(flowDefinitionCode, FlowDefinitionStatusEnum.DEPLOY.getCode());
        return flowDefinitionSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowDefinition(list.get(0));
        });
    }

    public Future<FlowDefinition> getByCodeAndStatus(String code, int status) {
        SqlAssist sqlAssist = FlowDefinition.codeStatusSqlAssist(code, status);
        return flowDefinitionSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowDefinition(list.get(0));
        });
    }

    /**
     * 删除流程
     * @param flowDefinitionId 流程定义Id
     * @return
     */
    public Future<Integer> deleteWithCheck(long flowDefinitionId) {
        return getById(flowDefinitionId).compose(flowDefinition -> {
            Integer status = flowDefinition.getStatus();
            final boolean canDelete = FlowDefinitionStatusEnum.INIT.getCode().equals(status) || FlowDefinitionStatusEnum.DRAFT.getCode().equals(status);
            if (!canDelete) {
                return Future.failedFuture("当前流程不是初始状态或者草稿状态");
            }

            return deleteGraph(flowDefinition.getId()).compose(cnt -> flowDefinitionSQL.deleteById(flowDefinition.getId()));
        });
    }

    public Future<Integer> deleteGraph(long flowDefinitionId) {
        SqlAssist sqlAssist = FlowDefinitionGraph.flowDefinitionIdSqlAssist(flowDefinitionId);
        return flowDefinitionGraphSQL.deleteByAssist(sqlAssist);
    }

    /**
     * 根据流程定义Id查询json
     * @param flowDefinitionId 流程定义Id
     * @return 流程定义json
     */
    public Future<FlowDefinitionGraph> getJsonByFlowDefinitionId(long flowDefinitionId) {
        SqlAssist sqlAssist = FlowDefinitionGraph.flowDefinitionIdSqlAssist(flowDefinitionId);
        return flowDefinitionGraphSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowDefinitionGraph(list.get(0));
        });
    }

    /**
     * 插入流程图
     * @param flowDefinitionId 流程定义Id
     * @param graph 流程json数据
     * @param x6Json 流程json数据，包括位置
     * @return json数据的id
     */
    public Future<Long> insertJson(long flowDefinitionId, String graph, String x6Json) {
        FlowDefinitionGraph flowDefinitionGraph = new FlowDefinitionGraph();
        flowDefinitionGraph.setFlowDefinitionId(flowDefinitionId);
        flowDefinitionGraph.setContent(graph);
        flowDefinitionGraph.setX6Json(x6Json);

        return flowDefinitionGraphSQL.insertNonEmptyGeneratedKeys(flowDefinitionGraph, MySQLClient.LAST_INSERTED_ID);
    }

    /**
     * 更新流程图
     * @param flowDefinitionId 流程定义Id
     * @param graph 流程图数据
     * @return 更新的cnt
     */
    public Future<Integer> updateGraph(long flowDefinitionId, String graph) {
        return getJsonByFlowDefinitionId(flowDefinitionId).compose(flowDefinitionGraph -> {
            if (flowDefinitionGraph == null) {
                return Future.failedFuture("根据流程Id无法查出对应的流程图");
            }

            FlowDefinitionGraph update = new FlowDefinitionGraph();
            flowDefinitionGraph.setId(flowDefinitionGraph.getId());
            flowDefinitionGraph.setContent(graph);

            return flowDefinitionGraphSQL.updateNonEmptyById(update);
        });
    }
}
