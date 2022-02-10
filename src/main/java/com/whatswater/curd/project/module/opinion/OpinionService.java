package com.whatswater.curd.project.module.opinion;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.permission.UserToken;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OpinionService {
    private OpinionSQL opinionSQL;
    private FlowEngineService flowEngineService;
    private UserTokenService userTokenService;

    public OpinionService() {
    }

    public Future<PageResult<Opinion>> search(Page page, OpinionQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return opinionSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return opinionSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(Opinion::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Opinion> getById(Long opinionId) {
        Future<JsonObject> result = opinionSQL.selectById(opinionId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new Opinion(json);
        });
    }

    public Future<List<Opinion>> queryByFlowInstanceId(Long flowInstanceId) {
        return flowEngineService.getFlowInstanceTaskService().queryByInstanceId(flowInstanceId)
            .<List<Long>>map(taskList -> {
                if (CollectionUtil.isEmpty(taskList)) {
                    return Collections.emptyList();
                }
                return taskList.stream().map(FlowInstanceTask::getId).collect(Collectors.toList());
            }).compose(taskIds -> {
                if (CollectionUtil.isEmpty(taskIds)) {
                    return Future.succeededFuture(Collections.emptyList());
                }

                SqlAssist sqlAssist = Opinion.flowInstanceTaskIdListSqlAssist(taskIds);
                return list(sqlAssist);
            });
    }

    public Future<List<OpinionVo>> queryVoByFlowInstanceId(Long flowInstanceId) {
        return flowEngineService.getFlowInstanceTaskService().queryByInstanceId(flowInstanceId)
            .compose(taskList -> {
                if (CollectionUtil.isEmpty(taskList)) {
                    return Future.succeededFuture(Collections.emptyList());
                }

                List<Long> taskIds = taskList.stream().map(FlowInstanceTask::getId).collect(Collectors.toList());
                SqlAssist sqlAssist = Opinion.flowInstanceTaskIdListSqlAssist(taskIds);
                return list(sqlAssist).compose(opinions -> {
                    List<Long> flowLinkIds = taskList.stream().map(FlowInstanceTask::getFlowLinkId).distinct().collect(Collectors.toList());
                    return flowEngineService.getFlowLinkService().listByIds(flowLinkIds).map(flowLinks -> Tuple2.of(opinions, flowLinks));
                }).map(tuple2 -> {
                    List<Opinion> opinions = tuple2._1;
                    List<FlowLink> flowLinks = tuple2._2;
                    List<OpinionVo> voList = new ArrayList<>(opinions.size());
                    for (Opinion opinion: opinions) {
                        OpinionVo vo = OpinionVo.fromOpinion(opinion);
                        for (FlowInstanceTask task: taskList) {
                            if (task.getId().equals(opinion.getFlowInstanceTaskId())) {
                                vo.setLinkCode(task.getFlowLinkCode());
                                for (FlowLink flowLink: flowLinks) {
                                    if (flowLink.getId().equals(task.getFlowLinkId())) {
                                        vo.setLinkName(flowLink.getTitle());
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        voList.add(vo);
                    }
                    voList.sort(Comparator.comparing(Opinion::getCreateTime));
                    return voList;
                });
            });
    }

    public Future<List<Opinion>> list(SqlAssist sqlAssist) {
        return opinionSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return Collections.emptyList();
            }

            return jsonList.stream().map(Opinion::new).collect(Collectors.toList());
        });
    }

    public Future<Long> fillAndInsertWithCheck(String token, Opinion opinion) {
        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            return Future.failedFuture("当前用户未登录，请重新登录");
        }

        Employee employee = userToken.getEmployee();
        opinion.setOpinionApplicant(employee.getLoginName());
        opinion.setOpinionApplicantName(employee.getName());
        opinion.setCreateTime(LocalDateTime.now());
        return insert(opinion);
    }

    public Future<Long> insert(Opinion opinion) {
        return opinionSQL.insertNonEmptyGeneratedKeys(opinion, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> updateWithCheck(String token, Opinion opinion) {
        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            return Future.failedFuture("当前用户未登录，请重新登录");
        }

        Opinion updateData = new Opinion();
        Employee employee = userToken.getEmployee();
        updateData.setOpinionApplicant(employee.getLoginName());
        updateData.setOpinionApplicantName(employee.getName());
        updateData.setId(opinion.getId());
        updateData.setContent(opinion.getContent());

        return update(updateData);
    }

    public Future<Integer> update(Opinion opinion) {
        return opinionSQL.updateNonEmptyById(opinion);
    }

    public void setOpinionSQL(OpinionSQL opinionSQL) {
        this.opinionSQL = opinionSQL;
    }

    public void setFlowEngineService(FlowEngineService flowEngineService) {
        this.flowEngineService = flowEngineService;
    }

    public void setUserTokenService(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }
}
