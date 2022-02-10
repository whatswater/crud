package com.whatswater.curd.project.module.workflow.flowEngine;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.BusinessException;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.CrudUtils.Tuple3;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.module.workflow.FlowConst;
import com.whatswater.curd.project.module.workflow.assignee.AssigneeConfig;
import com.whatswater.curd.project.module.workflow.assignee.AssigneeConfigService;
import com.whatswater.curd.project.module.workflow.event.UpdateTaskStatusContext;
import com.whatswater.curd.project.module.workflow.event.FlowSystemEvent;
import com.whatswater.curd.project.module.workflow.event.FlowSystemEventTypeEnum;
import com.whatswater.curd.project.module.workflow.event.GenerateTaskContext;
import com.whatswater.curd.project.module.workflow.flowConstant.FlowConstantService;
import com.whatswater.curd.project.module.workflow.flowDefinition.*;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEditorGraph.FlowEdge;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEditorGraph.FlowNode;
import com.whatswater.curd.project.module.workflow.flowEngine.context.ContextFlowInstanceStart;
import com.whatswater.curd.project.module.workflow.flowEngine.context.ContextFlowLinkStart;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstance;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstanceService;
import com.whatswater.curd.project.module.workflow.flowInstanceLinkActor.FlowInstanceLinkActor;
import com.whatswater.curd.project.module.workflow.flowInstanceLinkActor.FlowInstanceLinkActorService;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTaskService;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTaskStatusEnum;
import com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation.FlowInstanceTaskRelation;
import com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation.FlowInstanceTaskRelationService;
import com.whatswater.curd.project.module.workflow.flowInstanceVariable.FlowInstanceVariable;
import com.whatswater.curd.project.module.workflow.flowInstanceVariable.FlowInstanceVariableService;
import com.whatswater.curd.project.module.workflow.flowLink.*;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithCandidates.Candidate;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstant;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstantService;
import com.whatswater.curd.project.module.workflow.flowLinkRelation.FlowLinkRelation;
import com.whatswater.curd.project.module.workflow.flowLinkRelation.FlowLinkRelationService;
import com.whatswater.curd.project.sys.permission.UserToken;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowEngineService {
    FlowDefinitionService flowDefinitionService;
    FlowConstantService flowConstantService;
    FlowInstanceService flowInstanceService;
    FlowInstanceVariableService flowInstanceVariableService;

    FlowLinkService flowLinkService;
    FlowLinkConstantService flowLinkConstantService;
    FlowLinkRelationService flowLinkRelationService;

    FlowInstanceLinkActorService flowInstanceLinkActorService;
    FlowInstanceTaskService flowInstanceTaskService;
    FlowInstanceTaskRelationService flowInstanceTaskRelationService;

    WebClient workflowWebClient;
    AssigneeConfigService assigneeConfigService;

    public Future<FlowInstanceTask> getPrevTask(final FlowInstanceTask currentTask) {
        return flowInstanceTaskRelationService.getByNextTaskId(currentTask.getId()).compose(relation -> {
            if (relation == null) {
                return Future.failedFuture("任务关系为空");
            }
            Long prevTaskId = relation.getPrevTaskId();
            return flowInstanceTaskService.getById(prevTaskId);
        });
    }

    public Future<Integer> deployFlow(long flowDefinitionId) {
        Future<FlowDefinition> flowDefinitionFuture = flowDefinitionService.getById(flowDefinitionId);
        return flowDefinitionFuture.compose(flowDefinition -> {
            if (!FlowDefinitionStatusEnum.canDeploy(flowDefinition.getStatus())) {
                return Future.failedFuture("当前流程状态不是初始化，不能部署");
            }
            return flowDefinitionService
                .getJsonByFlowDefinitionId(flowDefinitionId)
                .map(flowDefinitionGraph -> CrudUtils.Tuple2.of(flowDefinition, flowDefinitionGraph));
        }).compose(tuple2 -> {
            FlowDefinition flowDefinition = tuple2._1;
            FlowDefinitionGraph flowDefinitionGraph = tuple2._2;
            FlowEditorGraph editorGraph = FlowEditorGraph.fromJson(flowDefinitionGraph.getContent());

            List<FlowNode> allNode = editorGraph.findAllNode();
            List<FlowEdge> allEdge = editorGraph.findAllEdge();

            final Map<String, Long> nodeId2LinkId = new TreeMap<>();
            List<SameFutureBuilder<FlowLink>> taskList = allNode
                .stream()
                .map(flowNode -> {
                    return (SameFutureBuilder<FlowLink>) resultList -> createAndInsertFlowLink(flowDefinition, flowNode, nodeId2LinkId);
                })
                .collect(Collectors.toList());

            return CrudUtils.serialTask(taskList).compose(flowLinks -> {
                List<SameFutureBuilder<Long>> insertEdgeList = allEdge.stream()
                    .map(edge -> {
                        return (SameFutureBuilder<Long>) resultList -> flowLinkRelationService.insert(createFlowLinkRelation(edge, nodeId2LinkId));
                    })
                    .collect(Collectors.toList());
                return CrudUtils.serialTask(insertEdgeList);
            });
        }).compose(list -> {
            FlowDefinition update = new FlowDefinition();
            update.setId(flowDefinitionId);
            update.setStatus(FlowDefinitionStatusEnum.DEPLOY.getCode());

            return flowDefinitionService.update(update);
        });
    }

    public Future<FlowLink> createAndInsertFlowLink(FlowDefinition flowDefinition, FlowNode node, Map<String, Long> nodeId2LinkId) {
        FlowLink flowLink = createFlowLink(flowDefinition, node);
        return flowLinkService.insert(flowLink).map(linkId -> {
            flowLink.setId(linkId);
            nodeId2LinkId.put(node.getId(), linkId);
            return flowLink;
        }).compose(ignored -> {
            List<SameFutureBuilder<Long>> taskList = createFlowLinkConstant(flowLink, node).stream()
                .map(flowLinkConstant ->  {
                    return (SameFutureBuilder<Long>)resultList -> flowLinkConstantService.insert(flowLinkConstant);
                })
                .collect(Collectors.toList());
            return CrudUtils.serialTask(taskList).map(flowLink);
        });
    }


    /**
     * 根据流程编码和版本号启动一个流程
     * @param context 启动流程context
     * @param flowDefinitionCode 流程定义Code
     * @return 流程实例异步对象
     */
    public Future<FlowInstance> startFlow(final ContextFlowInstanceStart context, String flowDefinitionCode) {
        Future<FlowDefinition> definitionFuture = flowDefinitionService.getByCode(flowDefinitionCode);
        return definitionFuture.compose(flowDefinition -> this.startFlow(context, flowDefinition));
    }

    public Future<FlowInstance> startFlow(final ContextFlowInstanceStart context, final FlowDefinition flowDefinition) {
        Future<FlowLinkWithConstant> startLinkFuture = flowLinkService.getFlowStartLink(flowDefinition.getId());
        return startLinkFuture.compose(startLink -> {
            final FlowInstance flowInstance = new FlowInstance();
            flowInstance.setFlowDefinitionId(flowDefinition.getId());
            flowInstance.setFlowDefinitionCode(flowDefinition.getFlowDefinitionCode());
            flowInstance.setFlowVersionNo(flowDefinition.getVersionNo());
            flowInstance.setStartTime(LocalDateTime.now());
            flowInstance.setStartUser(context.getStartUser());
            flowInstance.setStartType(context.getStartType());

            Future<Long> flowInstanceIdFuture = flowInstanceService.insert(flowInstance);
            return flowInstanceIdFuture
                .compose(flowInstanceId -> {
                    flowInstance.setId(flowInstanceId);
                    if (Objects.nonNull(context.getInitVariableTable())) {
                        return flowInstanceVariableService
                            .initFlowInstanceVariable(flowInstance.getId(), context.getInitVariableTable())
                            .map(list -> {
                                Map<String, FlowInstanceVariable> variableTable = list
                                    .stream()
                                    .collect(Collectors.toMap(FlowInstanceVariable::getVariableName, Function.identity(), (a1, a2) -> a1));
                                flowInstance.setVariableTable(variableTable);
                                return flowInstance;
                            });
                    } else {
                        return Future.succeededFuture(flowInstance);
                    }
                })
                .compose(instance -> {
                    ContextFlowLinkStart flowLinkStart = new ContextFlowLinkStart();
                    flowLinkStart.setPrevTask(null);
                    flowLinkStart.setBack(false);
                    return this.startFlowLink(flowLinkStart, instance, startLink).map(instance);
                });
        });
    }



    /**
     * 启动下一个环节
     * @param currentTask 当前任务
     * @param routerList 路由列表
     * @return 空
     */
    public Future<Void> routeFlowLink(final FlowInstanceTask currentTask, final Set<String> routerList) {
        return flowLinkRelationService.queryNextLinkRelation(currentTask.getFlowLinkId())
            .map(flowLinkRelations -> flowLinkRelations.stream()
                .filter(flowLinkRelation -> routerList.contains(flowLinkRelation.getRouteName()))
                .map(FlowLinkRelation::getEndLinkId).distinct().collect(Collectors.toList())
            ).compose(nextLinkIds -> CrudUtils.serialTask(
                t1 -> flowLinkService.listByIds(nextLinkIds),
                t2 -> flowInstanceService.getById(currentTask.getFlowInstanceId()).compose(flowInstanceVariableService::withVariable)
            )).compose(tuple -> {
                List<SameFutureBuilder<List<FlowInstanceTask>>> startLinkTask = new ArrayList<>(tuple._1.size());
                for (FlowLink flowLink: tuple._1) {
                    startLinkTask.add(list -> {
                        return flowLinkService.withConstant(flowLink)
                            .compose(constant -> {
                                ContextFlowLinkStart context = new ContextFlowLinkStart();
                                context.setBack(false);
                                context.setPrevTask(currentTask);
                                return this.startFlowLink(context, tuple._2, constant);
                            });
                    });
                }
                return CrudUtils.serialTask(startLinkTask).mapEmpty();
            });
    }

    public Future<List<Long>> setActorListOfLink(FlowInstanceTask prevTask, NextLinkActorInfo nextLinkActorInfo) {
        List<String> actorList = nextLinkActorInfo.getActorList();
        if (CollectionUtil.isEmpty(actorList)) {
            return Future.succeededFuture(Collections.emptyList());
        }

        List<String> actorNameList = nextLinkActorInfo.getActorNameList();
        List<FlowInstanceLinkActor> actors = new ArrayList<>(actorList.size());
        for (int i = 0; i < actorList.size(); i++) {
            String actor = actorList.get(i);

            FlowInstanceLinkActor tmp = new FlowInstanceLinkActor();
            tmp.setActor(actor);
            tmp.setActorName(actorNameList.get(i));
            tmp.setFlowLinkId(nextLinkActorInfo.getNextLinkId());
            tmp.setFlowLinkCode(nextLinkActorInfo.getNextLinkCode());
            tmp.setFlowInstanceId(prevTask.getFlowInstanceId());
            tmp.setPrevTaskId(prevTask.getId());
            actors.add(tmp);
        }
        return flowInstanceLinkActorService.batchInsert(actors);
    }

    public Future<Set<String>> getActorListOfLink(FlowInstanceTask prevTask, FlowInstance flowInstance, FlowLink flowLink) {
        return flowInstanceLinkActorService.listBy(prevTask.getId(), flowLink.getId()).compose(list -> {
            if (CollectionUtil.isEmpty(list)) {
                return flowLinkService.withConstant(flowLink)
                    .compose(flowLinkWithConstant -> assigneeConfigService.getAssignee(flowInstance, flowLinkWithConstant))
                    .map(CrudUtils.Tuple2::second);
            }
            return Future.succeededFuture(list.stream().map(FlowInstanceLinkActor::getActor).collect(Collectors.toSet()));
        });
    }

    public Future<List<FlowInstanceTask>> insertTaskRelationList(FlowInstanceTask prevTask, List<FlowInstanceTask> taskList) {
        if (prevTask == null) {
            return Future.succeededFuture(taskList);
        }
        return flowInstanceTaskRelationService.batchInsert(prevTask.getId(), taskList).map(taskList);
    }

    /**
     * 开始执行流程环节
     * @param context 流程环节启动context
     * @param flowInstance 流程实例
     * @param flowLink 流程环节配置
     * @return 流程实例任务
     */
    public Future<List<FlowInstanceTask>> startFlowLink(final ContextFlowLinkStart context, final FlowInstance flowInstance, final FlowLinkWithConstant flowLink) {
        final String linkName = flowInstance.getFlowDefinitionCode() + StrUtil.DOT + flowLink.getFlowLinkCode();

        String type = flowLink.getType();
        FlowLinkType flowLinkType = FlowLinkType.findByCode(type);
        if (flowLinkType == null) {
            throw new BusinessException("不支持的流程环节类型：" + type);
        }

        switch (flowLinkType) {
            case START:
            case END:
            {
                FlowInstanceTask flowInstanceTask = createNonActorTask(flowInstance, flowLink, flowLinkType);
                return flowInstanceTaskService.insert(flowInstanceTask).map(id -> {
                    flowInstanceTask.setId(id);
                    return Collections.singletonList(flowInstanceTask);
                }).compose(list -> this.insertTaskRelationList(context.getPrevTask(), list)).compose(list -> {
                    return this.executeLinkEvent(flowLink, FlowLinkTriggerEnum.BEFORE_EACH.getConstantName()).map(list);
                }).compose(list -> {
                    return this.executeGenerateTaskSystemEvent(flowInstance, list).map(list);
                }).compose(task -> this.completeTask(task.get(0).getId()).map(task));
            }
            case NORMAL:
                return getActorListOfLink(context.getPrevTask(), flowInstance, flowLink).compose(assigneeList -> {
                    if (CollectionUtil.isEmpty(assigneeList) || assigneeList.size() > 1) {
                        return Future.failedFuture("正常环节：" + linkName + "参与者应当有且只有一个");
                    }
                    String assignee = new ArrayList<>(assigneeList).get(0);
                    FlowInstanceTask flowInstanceTask = new FlowInstanceTask();
                    flowInstanceTask.setType(FlowLinkType.NORMAL.getId());
                    flowInstanceTask.setFlowInstanceId(flowInstance.getId());
                    flowInstanceTask.setFlowLinkId(flowLink.getId());
                    flowInstanceTask.setFlowLinkCode(flowLink.getFlowLinkCode());
                    flowInstanceTask.setCreateTime(LocalDateTime.now());
                    flowInstanceTask.setActor(assignee);
                    flowInstanceTask.setStatus(FlowInstanceTaskStatusEnum.INIT.getId());

                    return flowInstanceTaskService.insert(flowInstanceTask).map(id -> {
                        flowInstanceTask.setId(id);
                        return Collections.singletonList(flowInstanceTask);
                    }).compose(list -> this.insertTaskRelationList(context.getPrevTask(), list)).compose(list -> {
                        return this.executeLinkEvent(flowLink, FlowLinkTriggerEnum.BEFORE_EACH.getConstantName()).map(list);
                    }).compose(list -> {
                        return this.executeGenerateTaskSystemEvent(flowInstance, list).map(list);
                    });
                });
            case CONCURRENT:
            case LOCK:
                return getActorListOfLink(context.getPrevTask(), flowInstance, flowLink).compose(assigneeList -> {
                    if (CollectionUtil.isEmpty(assigneeList)) {
                        return Future.failedFuture("环节：" + linkName + "参与者为空");
                    }

                    final List<FlowInstanceTask> flowInstanceTaskList = assigneeList.stream().map(assignee -> {
                        FlowInstanceTask flowInstanceTask = new FlowInstanceTask();
                        flowInstanceTask.setType(flowLinkType.getId());
                        flowInstanceTask.setFlowInstanceId(flowInstance.getId());
                        flowInstanceTask.setFlowLinkId(flowLink.getId());
                        flowInstanceTask.setFlowLinkCode(flowLink.getFlowLinkCode());
                        flowInstanceTask.setCreateTime(LocalDateTime.now());
                        flowInstanceTask.setActor(assignee);
                        flowInstanceTask.setStatus(FlowInstanceTaskStatusEnum.INIT.getId());

                        return flowInstanceTask;
                    }).collect(Collectors.toList());

                    return CrudUtils.serialTask(flowInstanceTaskList.stream().map(task -> {
                            return (SameFutureBuilder<Long>) idList -> flowInstanceTaskService.insert(task);
                        }).collect(Collectors.toList())
                    ).map(idList -> {
                        for (int i = 0; i < flowInstanceTaskList.size(); i++) {
                            FlowInstanceTask flowInstanceTask = flowInstanceTaskList.get(i);
                            flowInstanceTask.setId(idList.get(i));
                        }
                        return flowInstanceTaskList;
                    }).compose(list -> this.insertTaskRelationList(context.getPrevTask(), list)).compose(taskList -> {
                        return this.executeLinkEvent(flowLink, FlowLinkTriggerEnum.BEFORE_EACH.getConstantName()).map(taskList);
                    }).compose(taskList -> {
                        return this.executeGenerateTaskSystemEvent(flowInstance, taskList).map(taskList);
                    });
                });
            case JOIN:
                return flowLinkRelationService.queryPrevLinkId(flowLink.getId()).compose(prevLinkIds -> {
                    List<SameFutureBuilder<List<FlowInstanceTask>>> sameFutureBuilderList = new ArrayList<>(prevLinkIds.size());
                    for (Long prevLinkId: prevLinkIds) {
                        SameFutureBuilder<List<FlowInstanceTask>> sameFutureBuilder = preTaskList -> flowInstanceTaskService.queryFlowInstanceTask(prevLinkId);
                        sameFutureBuilderList.add(sameFutureBuilder);
                    }
                    return CrudUtils.serialTask(sameFutureBuilderList);
                }).compose(taskList -> {
                    for (List<FlowInstanceTask> list: taskList) {
                        if (CollectionUtil.isEmpty(list)) {
                            return Future.succeededFuture(Collections.emptyList());
                        }
                        for (FlowInstanceTask task: list) {
                            if (!FlowInstanceTaskStatusEnum.isComplete(task.getStatus())) {
                                return Future.succeededFuture(Collections.emptyList());
                            }
                        }
                    }

                    FlowInstanceTask flowInstanceTask = createNonActorTask(flowInstance, flowLink, flowLinkType);
                    return flowInstanceTaskService.insert(flowInstanceTask).map(id -> {
                        flowInstanceTask.setId(id);
                        return Collections.singletonList(flowInstanceTask);
                    }).compose(list -> this.insertTaskRelationList(context.getPrevTask(), list)).compose(list -> {
                        return this.executeLinkEvent(flowLink, FlowLinkTriggerEnum.BEFORE_EACH.getConstantName()).map(list);
                    }).compose(list -> {
                        return this.executeGenerateTaskSystemEvent(flowInstance, list).map(list);
                    }).compose(task -> this.completeTask(task.get(0).getId()).map(task));
                });
            default:
                throw new BusinessException("不支持的流程环节类型：" + type);
        }
    }

    public Future<List<FlowInstanceTask>> backFlowLink(Long taskId, final Long backLinkId) {
        return flowInstanceTaskService.getById(taskId).compose(task -> {
            if (Objects.isNull(task)) {
                return Future.failedFuture("完成任务时，根据任务Id查询的任务为空");
            }
            return flowInstanceService.getById(task.getFlowInstanceId())
                .compose(flowInstanceVariableService::withVariable)
                .compose(flowInstance -> {
                    return flowInstanceTaskService.updateTaskStatus(taskId, FlowInstanceTaskStatusEnum.COMPLETE)
                        .compose(cnt -> this.executeLinkEvent(task.getFlowLinkId(), FlowLinkTriggerEnum.AFTER_EACH.getConstantName()))
                        .map(flowInstance);
                }).compose(flowInstance -> {
                    return this.executeCompleteTaskSystemEvent(flowInstance, taskId).map(Tuple2.of(flowInstance, task));
                });
        }).compose(data -> {
            FlowInstance flowInstance = data._1;
            return CrudUtils.serialTask(t -> flowInstanceTaskService.queryByInstanceId(flowInstance.getId()), t -> flowInstanceTaskRelationService.listBy(flowInstance.getId()))
                .compose(tuple -> {
                    List<FlowInstanceTask> taskList = tuple._1;
                    List<FlowInstanceTaskRelation> taskRelationList = tuple._2;

                    // todo 判断是否允许回退
                    // todo 插入参与者数据

                    return Future.succeededFuture();
                }).map(data);
        }).compose(data -> {
            return flowLinkService.getById(backLinkId).compose(flowLinkService::withConstant).compose(flowLinkWithConstant -> {
                ContextFlowLinkStart context = new ContextFlowLinkStart();
                context.setPrevTask(data._2);
                context.setBack(true);
                return this.startFlowLink(context, data._1, flowLinkWithConstant);
            });
        });
    }

    public Future<FlowInstanceTask> cancelTask(final long taskId) {
        return flowInstanceTaskService.getById(taskId).compose(task -> {
            if (Objects.isNull(task)) {
                return Future.failedFuture("取消任务时，根据任务Id查询的任务为空");
            }
            return flowInstanceService.getById(task.getFlowInstanceId())
                .compose(flowInstanceVariableService::withVariable)
                .compose(flowInstance -> {
                    return flowInstanceTaskService.updateTaskStatus(taskId, FlowInstanceTaskStatusEnum.CANCEL).map(flowInstance);
                }).compose(flowInstance -> {
                    return this.executeCancelTaskSystemEvent(flowInstance, taskId).map(task);
                });
        });
    }



//    public Future<FlowInstanceTask> commit(Long taskId, List<NextLinkActorInfo> nextLinkActorInfo) {
//        return flowInstanceTaskService.getById(taskId).compose(task -> {
//            // flowEngineService.getFlowLinkService().getFlowStartLink();
//
//            return this.setActorListOfLink(task, nextLinkActorInfo);
//        }).compose(v -> this.completeTask(taskId));
//    }

    public Future<FlowInstanceTask> commit(Long taskId, NextLinkActorInfo nextLinkActorInfo) {
        return flowInstanceTaskService.getById(taskId).compose(task -> {
            return this.setActorListOfLink(task, nextLinkActorInfo);
        }).compose(v -> this.completeTask(taskId));
    }

    /**
     * 根据taskId完成task任务
     * @param taskId 任务Id
     * @return 当前任务
     */
    public Future<FlowInstanceTask> completeTask(final long taskId) {
        return flowInstanceTaskService.getById(taskId).compose(task -> {
            if (Objects.isNull(task)) {
                return Future.failedFuture("完成任务时，根据任务Id查询的任务为空");
            }

            return flowInstanceService.getById(task.getFlowInstanceId())
                .compose(flowInstanceVariableService::withVariable)
                .compose(flowInstance -> {
                    return flowInstanceTaskService.updateTaskStatus(taskId, FlowInstanceTaskStatusEnum.COMPLETE)
                        .compose(cnt -> this.executeLinkEvent(task.getFlowLinkId(), FlowLinkTriggerEnum.AFTER_EACH.getConstantName()))
                        .map(flowInstance);
                }).compose(flowInstance -> {
                    return this.executeCompleteTaskSystemEvent(flowInstance, taskId).map(Tuple2.of(flowInstance, task));
                });
        }).compose(t2 -> {
            Long flowLinkId = t2._2.getFlowLinkId();
            return flowInstanceTaskService.queryFlowInstanceTask(flowLinkId).map(taskList -> Tuple3.of(t2, taskList));
        }).compose(data -> {
            // 当此环节所有任务均完成时，执行事件
            for (FlowInstanceTask task: data._3) {
                if (taskId == task.getId()) {
                    continue;
                }
                if (!FlowInstanceTaskStatusEnum.isComplete(task.getStatus())) {
                    return Future.succeededFuture(data);
                }
            }
            return this
                .executeLinkEvent(data._2.getFlowLinkId(), FlowLinkTriggerEnum.AFTER_ALL.getConstantName())
                .map(data);
        }).compose(data -> {
            FlowInstance instance = data._1;
            String router = instance.getVariableValue(data._2.getFlowLinkCode() + StrUtil.DOT + FlowConst.FLOW_VARIABLE_NEXT_LINK_ROUTER);
            Set<String> routerList = new TreeSet<>();
            if (StrUtil.isEmpty(router)) {
                routerList.add(FlowConst.ROUTER_DEFAULT);
            } else {
                routerList.addAll(Arrays.asList(router.split(StrUtil.COMMA)));
            }
            return this.routeFlowLink(data._2, routerList).map(data._2);
        });
    }

    private FlowInstanceTask createNonActorTask(final FlowInstance flowInstance, final FlowLink flowLink, final FlowLinkType flowLinkType) {
        FlowInstanceTask flowInstanceTask = new FlowInstanceTask();
        flowInstanceTask.setType(flowLinkType.getId());
        if (FlowLinkType.START.equals(flowLinkType)) {
            flowInstanceTask.setFlowLinkCode(FlowConst.LINK_CODE_START);
        } else if (FlowLinkType.END.equals(flowLinkType)) {
            flowInstanceTask.setFlowLinkCode(FlowConst.LINK_CODE_END);
        }
        flowInstanceTask.setFlowLinkId(flowLink.getId());
        flowInstanceTask.setFlowInstanceId(flowInstance.getId());
        flowInstanceTask.setCreateTime(LocalDateTime.now());
        flowInstanceTask.setActor(StrUtil.EMPTY);
        flowInstanceTask.setStatus(FlowInstanceTaskStatusEnum.INIT.getId());

        return flowInstanceTask;
    }

    public Future<String> executeSystemEvent(FlowSystemEvent systemEvent) {
        return workflowWebClient.post(systemEvent.getType().getUrl())
            .putHeader(CrudConst.HEADER_TOKEN, CrudConst.WORKFLOW_TOKEN)
            .sendJson(Buffer.buffer(CrudUtils.toJson(systemEvent.getContext())))
            .map(response -> {
                JsonObject json = response.bodyAsJsonObject();
                RestResult<String> result = RestResult.stringFromJsonObject(json);
                if (!result.isSuccess()) {
                    throw new BusinessException("调用事件接口失败, 错误码: " + result.getCode() + ", 错误信息: " + result.getMsg());
                }
                return result.getData();
            });
    }

    public Future<String> executeGenerateTaskSystemEvent(final FlowInstance flowInstance, final List<FlowInstanceTask> flowInstanceTaskList) {
        FlowSystemEvent systemEvent = new FlowSystemEvent();
        systemEvent.setType(FlowSystemEventTypeEnum.GENERATE_TASK);

        GenerateTaskContext generateTaskContext = new GenerateTaskContext();
        generateTaskContext.setFlowInstanceTask(flowInstanceTaskList);

        Map<String, FlowInstanceVariable> variableTable = flowInstance.getVariableTable();
        if (variableTable != null) {
            Map<String, String> contextVariableTable = new TreeMap<>();
            for (Map.Entry<String, FlowInstanceVariable> entry: variableTable.entrySet()) {
                contextVariableTable.put(entry.getKey(), entry.getValue().getVariableValue());
            }
            generateTaskContext.setInstanceVariable(contextVariableTable);
        }
        systemEvent.setContext(generateTaskContext);
        return this.executeSystemEvent(systemEvent);
    }

    public Future<String> executeCancelTaskSystemEvent(final FlowInstance flowInstance, Long taskId) {
        FlowSystemEvent systemEvent = new FlowSystemEvent();
        systemEvent.setType(FlowSystemEventTypeEnum.CANCEL_TASK);

        UpdateTaskStatusContext updateTaskStatusContext = new UpdateTaskStatusContext();
        updateTaskStatusContext.setTaskId(taskId);

        String businessId = flowInstance.getVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_ID);
        String businessType = flowInstance.getVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_TYPE);
        updateTaskStatusContext.setBusinessId(businessId);
        updateTaskStatusContext.setBusinessType(businessType);

        systemEvent.setContext(updateTaskStatusContext);
        return this.executeSystemEvent(systemEvent);
    }

    public Future<String> executeCompleteTaskSystemEvent(final FlowInstance flowInstance, Long taskId) {
        FlowSystemEvent systemEvent = new FlowSystemEvent();
        systemEvent.setType(FlowSystemEventTypeEnum.COMPLETE_TASK);

        UpdateTaskStatusContext updateTaskStatusContext = new UpdateTaskStatusContext();
        updateTaskStatusContext.setTaskId(taskId);

        String businessId = flowInstance.getVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_ID);
        String businessType = flowInstance.getVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_TYPE);
        updateTaskStatusContext.setBusinessId(businessId);
        updateTaskStatusContext.setBusinessType(businessType);

        systemEvent.setContext(updateTaskStatusContext);
        return this.executeSystemEvent(systemEvent);
    }

    /**
     * 执行环节事件防范
     * @param flowLinkId 流程环节Id
     * @param trigger 事件触发时机
     *  @see FlowLinkTriggerEnum
     * @return 事件的执行结果
     */
    public Future<List<String>> executeLinkEvent(long flowLinkId, String trigger) {
        return flowLinkService.getWithConstantById(flowLinkId).compose(flowLinkWithConstant -> executeLinkEvent(flowLinkWithConstant, trigger));
    }

    /**
     * 执行事件方法
     * @param flowLink 流程环节
     * @param trigger 事件触发时机
     *  @see FlowLinkTriggerEnum
     * @return 事件的执行结果
     */
    public Future<List<String>> executeLinkEvent(FlowLinkWithConstant flowLink, String trigger) {
        String triggerConfig = flowLink.getConfigValue(trigger);
        if (StrUtil.isEmpty(triggerConfig)) {
            return Future.succeededFuture(Collections.emptyList());
        }

        List<FlowLinkEvent> eventList = CrudUtils.readValue(triggerConfig, new TypeReference<List<FlowLinkEvent>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });

        List<SameFutureBuilder<String>> sameFutureBuilderList = new ArrayList<>();
        for (FlowLinkEvent event: eventList) {
            String type = event.getType();
            FlowLinkEventTypeEnum typeEnum = FlowLinkEventTypeEnum.getByName(type);
            if (typeEnum == null) {
                throw new BusinessException("不支持的事件类型");
            }

            if (typeEnum == FlowLinkEventTypeEnum.HTTP) {
                String url = event.getEndpoint();
                sameFutureBuilderList.add(objectList -> workflowWebClient
                    .post(url)
                    .putHeader(CrudConst.HEADER_TOKEN, CrudConst.WORKFLOW_TOKEN)
                    .sendJson(Buffer.buffer(CrudUtils.toJson(flowLink)))
                    .map(response -> {
                        JsonObject json = response.bodyAsJsonObject();
                        RestResult<String> result = RestResult.stringFromJsonObject(json);
                        if (!result.isSuccess()) {
                            throw new BusinessException("调用事件接口失败, 错误码: " + result.getCode() + ", 错误信息: " + result.getMsg());
                        }
                        return result.getData();
                    })
                );
            }
        }
        return CrudUtils.serialTask(sameFutureBuilderList);
    }

    public Future<Tuple3<FlowInstanceTask, List<FlowLinkRelation>, List<FlowLink>>> nextLinkList(Long taskId) {
        return flowInstanceTaskService.getById(taskId).compose(task -> {
            Long flowLinkId = task.getFlowLinkId();
            return flowLinkRelationService.queryNextLinkRelation(flowLinkId).compose(flowLinkRelations -> {
                List<Long> flowLinkIds = flowLinkRelations.stream().map(FlowLinkRelation::getEndLinkId).distinct().collect(Collectors.toList());
                return flowLinkService.listByIds(flowLinkIds).map(flowLinkList -> Tuple3.of(task, flowLinkRelations, flowLinkList));
            });
        });
    }

    public static FlowLink createFlowLink(FlowDefinition flowDefinition, FlowNode node) {
        FlowLink flowLink = new FlowLink();
        flowLink.setFlowLinkCode(node.getCode());
        flowLink.setFlowDefinitionId(flowDefinition.getId());
        flowLink.setFlowDefinitionCode(flowDefinition.getFlowDefinitionCode());
        flowLink.setTitle(node.getTitle());
        flowLink.setType(node.getShape());
        flowLink.setRemark(StrUtil.EMPTY);
        flowLink.setCreateTime(LocalDateTime.now());

        return flowLink;
    }

    public static List<FlowLinkConstant> createFlowLinkConstant(FlowLink flowLink, FlowNode node) {
        FlowLinkConstant assignee = new FlowLinkConstant();
        assignee.setFlowLinkId(flowLink.getId());
        assignee.setConstantName(FlowConst.LINK_CONSTANT_PREFIX_ASSIGNEE);

        AssigneeConfig config = new AssigneeConfig();
        config.setConfig(node.getAssigneeConfig());
        config.setType(node.getAssigneeConfigType());
        assignee.setConstantValue(CrudUtils.toJson(config));

        assignee.setCreateTime(LocalDateTime.now());
        assignee.setRemark(StrUtil.EMPTY);

        List<FlowLinkEventWithTrigger> triggerList = node.getFlowLinkEventList();
        if (CollectionUtil.isEmpty(triggerList)) {
            return Collections.singletonList(assignee);
        }

        List<FlowLinkConstant> constantList = new ArrayList<>();
        constantList.add(assignee);
        Map<String, List<FlowLinkEventWithTrigger>> map = triggerList.stream().collect(Collectors.groupingBy(FlowLinkEventWithTrigger::getTriggerName));
        for (Map.Entry<String, List<FlowLinkEventWithTrigger>> entry: map.entrySet()) {
            FlowLinkConstant eventConst = new FlowLinkConstant();
            eventConst.setFlowLinkId(flowLink.getId());
            eventConst.setConstantName(entry.getKey());

            List<FlowLinkEvent> flowLinkEventList = entry.getValue().stream().map(FlowLinkEventWithTrigger::toFlowLinkEvent).collect(Collectors.toList());
            eventConst.setConstantValue(CrudUtils.toJson(flowLinkEventList));

            eventConst.setCreateTime(LocalDateTime.now());
            eventConst.setRemark(StrUtil.EMPTY);

            constantList.add(eventConst);
        }

        return constantList;
    }

    public static FlowLinkRelation createFlowLinkRelation(FlowEdge edge, Map<String, Long> nodeId2LinkId) {
        FlowLinkRelation linkRelation = new FlowLinkRelation();
        linkRelation.setStartLinkId(nodeId2LinkId.get(edge.getSourceNode().getId()));
        linkRelation.setEndLinkId(nodeId2LinkId.get(edge.getTargetNode().getId()));
        linkRelation.setRouteName(edge.getRouteName());
        linkRelation.setCreateTime(LocalDateTime.now());
        linkRelation.setRemark(StrUtil.EMPTY);

        return linkRelation;
    }

    public FlowDefinitionService getFlowDefinitionService() {
        return flowDefinitionService;
    }

    public FlowConstantService getFlowConstantService() {
        return flowConstantService;
    }

    public FlowInstanceService getFlowInstanceService() {
        return flowInstanceService;
    }

    public FlowInstanceVariableService getFlowInstanceVariableService() {
        return flowInstanceVariableService;
    }

    public FlowLinkService getFlowLinkService() {
        return flowLinkService;
    }

    public FlowLinkConstantService getFlowLinkConstantService() {
        return flowLinkConstantService;
    }

    public FlowLinkRelationService getFlowLinkRelationService() {
        return flowLinkRelationService;
    }

    public FlowInstanceTaskService getFlowInstanceTaskService() {
        return flowInstanceTaskService;
    }

    public FlowInstanceTaskRelationService getFlowInstanceTaskRelationService() {
        return flowInstanceTaskRelationService;
    }

    public WebClient getWorkflowWebClient() {
        return workflowWebClient;
    }

    public AssigneeConfigService getAssigneeConfigService() {
        return assigneeConfigService;
    }

    public void setFlowDefinitionService(FlowDefinitionService flowDefinitionService) {
        this.flowDefinitionService = flowDefinitionService;
    }

    public void setFlowConstantService(FlowConstantService flowConstantService) {
        this.flowConstantService = flowConstantService;
    }

    public void setFlowInstanceService(FlowInstanceService flowInstanceService) {
        this.flowInstanceService = flowInstanceService;
    }

    public void setFlowInstanceVariableService(FlowInstanceVariableService flowInstanceVariableService) {
        this.flowInstanceVariableService = flowInstanceVariableService;
    }

    public void setFlowLinkService(FlowLinkService flowLinkService) {
        this.flowLinkService = flowLinkService;
    }

    public void setFlowLinkConstantService(FlowLinkConstantService flowLinkConstantService) {
        this.flowLinkConstantService = flowLinkConstantService;
    }

    public void setFlowLinkRelationService(FlowLinkRelationService flowLinkRelationService) {
        this.flowLinkRelationService = flowLinkRelationService;
    }

    public void setFlowInstanceLinkActorService(FlowInstanceLinkActorService flowInstanceLinkActorService) {
        this.flowInstanceLinkActorService = flowInstanceLinkActorService;
    }

    public void setFlowInstanceTaskService(FlowInstanceTaskService flowInstanceTaskService) {
        this.flowInstanceTaskService = flowInstanceTaskService;
    }

    public void setFlowInstanceTaskRelationService(FlowInstanceTaskRelationService flowInstanceTaskRelationService) {
        this.flowInstanceTaskRelationService = flowInstanceTaskRelationService;
    }

    public void setWorkflowWebClient(WebClient workflowWebClient) {
        this.workflowWebClient = workflowWebClient;
    }

    public void setAssigneeConfigService(AssigneeConfigService assigneeConfigService) {
        this.assigneeConfigService = assigneeConfigService;
    }
}
