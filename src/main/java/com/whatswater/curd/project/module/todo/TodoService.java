package com.whatswater.curd.project.module.todo;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.CrudUtils.Tuple3;
import com.whatswater.curd.project.common.ErrorCodeEnum;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.FlowConst;
import com.whatswater.curd.project.module.workflow.event.GenerateTaskContext;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.uid.UidGeneratorService;
import com.whatswater.sql.executor.ContextService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class TodoService implements ContextService<TodoService> {
    private UidGeneratorService uidGeneratorService;
    private TodoSQL todoSQL;
    private FlowEngineService flowEngineService;
    private EmployeeService employeeService;

    public TodoService() {

    }

    public Future<PageResult<Todo>> search(final Page page, TodoQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return todoSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return todoSQL.selectAll(sqlAssist).map(list -> {
                    List<Todo> todoList = list.stream().map(Todo::new).collect(Collectors.toList());
                    return PageResult.of(todoList, page, total);
                });
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<Todo> getByTaskId(long taskId) {
        SqlAssist sqlAssist = Todo.taskIdSqlAssist(taskId);

        return todoSQL.selectAll(sqlAssist).map(jsonList -> {
            if (CollectionUtil.isEmpty(jsonList)) {
                return null;
            }
            return new Todo(jsonList.get(0));
        });
    }

    public Future<List<Todo>> onTaskCreated(GenerateTaskContext context) {
        List<SameFutureBuilder<Todo>> taskBuilderList = context.getFlowInstanceTask()
            .stream()
            .map(flowInstanceTask -> (SameFutureBuilder<Todo>)r -> {
                return this.onTaskCreated(context, flowInstanceTask);
            })
            .collect(Collectors.toList());
        return CrudUtils.serialTask(taskBuilderList);
    }

    public Future<Todo> onTaskCreated(GenerateTaskContext context, FlowInstanceTask flowInstanceTask) {
        if (FlowConst.LINK_CODE_START.equals(flowInstanceTask.getFlowLinkCode())
            || FlowConst.LINK_CODE_END.equals(flowInstanceTask.getFlowLinkCode())) {
            return Future.succeededFuture(null);
        }

        return flowEngineService.getPrevTask(flowInstanceTask)
            .compose(prevTask -> flowEngineService.getFlowLinkService().getById(prevTask.getFlowLinkId()).map(prevLink -> Tuple2.of(prevTask, prevLink)))
            .<Tuple3<FlowInstanceTask, FlowLink, Employee>>compose(tuple2 -> {
                FlowInstanceTask prevTask = tuple2._1;
                String actor = prevTask.getActor();
                if (StrUtil.isEmpty(actor)) {
                    return Future.succeededFuture(Tuple3.of(tuple2, null));
                }

                return employeeService.getByLoginName(actor).map(prevEmployee -> {
                    return Tuple3.of(tuple2, prevEmployee);
                });
            }).compose(tuple3 -> {
                FlowInstanceTask prevTask = tuple3._1;
                FlowLink prevLink = tuple3._2;
                Employee prevEmployee = tuple3._3;

                Todo todo = new Todo();
                todo.setTitle(context.getFlowVariableValue(FlowConst.FLOW_VARIABLE_TITLE));
                todo.setPrevLink(prevLink.getTitle());
                todo.setPrevEmployee(prevTask.getActor());
                if (prevEmployee != null) {
                    todo.setPrevEmployeeName(prevEmployee.getName());
                }
                todo.setActorEmployee(flowInstanceTask.getActor());

                todo.setModuleName(context.getFlowVariableValue(FlowConst.FLOW_VARIABLE_MODULE_NAME));
                todo.setBusinessType(context.getFlowVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_TYPE));
                todo.setBusinessId(context.getFlowVariableValue(FlowConst.FLOW_VARIABLE_BUSINESS_ID));
                todo.setTaskId(flowInstanceTask.getId());

                JsonObject extraInfo = new JsonObject();
                extraInfo.put("flowInstanceId", flowInstanceTask.getFlowInstanceId());
                extraInfo.put("flowLinkId", flowInstanceTask.getFlowLinkId());

                todo.setExtraInfo(extraInfo.encode());
                todo.setStatus(TodoStatusEnum.UNREAD.getCode());
                todo.setCreateTime(LocalDateTime.now());

                return employeeService.getByLoginName(flowInstanceTask.getActor()).map(employee -> {
                    if (employee != null) {
                        todo.setActorEmployeeName(employee.getLoginName());
                    }
                    return todo;
                }).compose(this::insert);
            });
    }

    public Future<Todo> insert(final Todo todo) {
        todo.setCreateTime(LocalDateTime.now());
        todo.setUpdateTraceId(uidGeneratorService.nextId());
        return todoSQL.insertNonEmptyGeneratedKeys(todo, MySQLClient.LAST_INSERTED_ID).map(id -> {
            todo.setId(id);
            return todo;
        });
    }

    private Future<Integer> updateTodo(final Todo todo) {
        todo.setUpdateTraceId(uidGeneratorService.nextId());
        return todoSQL.updateNonEmptyById(todo);
    }

    public Future<Integer> updateTodoStatus(final long id, int status) {
        if (!CrudUtils.gtZero(id)) {
            throw ErrorCodeEnum.UPDATE_ID_NULL.toException("更新待办状态时Id为空");
        }
        Todo todo = new Todo();
        todo.setId(id);
        todo.setStatus(status);
        return updateTodo(todo);
    }

    public Future<Integer> cancelTodo(final long taskId) {
        return getByTaskId(taskId).compose(todo -> {
            if (todo == null) {
                return Future.succeededFuture(0);
            }
            return updateTodoStatus(todo.getStatus(), TodoStatusEnum.CANCEL.getCode()).compose(cnt -> {
                if (cnt <= 0) {
                    return Future.failedFuture(ErrorCodeEnum.UPDATE_NON_DATA.toException("取消待办时更新数据为0"));
                }
                return Future.succeededFuture(cnt);
            });
        });
    }

    public Future<Integer> readTodo(final long id) {
        return updateTodoStatus(id, 2).compose(cnt -> {
            if (cnt <= 0) {
                return Future.failedFuture(ErrorCodeEnum.UPDATE_NON_DATA.toException("已读待办时更新数据为0"));
            }
            return Future.succeededFuture(cnt);
        });
    }

    public Future<Integer> completeTodo(final long taskId) {
        return getByTaskId(taskId).compose(todo -> {
            if (todo == null) {
                return Future.succeededFuture(0);
            }
            return updateTodoStatus(todo.getId(), TodoStatusEnum.COMPLETE.getCode()).compose(cnt -> {
                if (cnt <= 0) {
                    return Future.failedFuture(ErrorCodeEnum.UPDATE_NON_DATA.toException("完成待办时更新数据为0"));
                }
                return Future.succeededFuture(cnt);
            });
        });
    }

    public Future<TodoDetail> getTodoDetail(Long todoId) {
        return getById(todoId).compose(todo -> {
            if (todo == null) {
                return Future.failedFuture("未查询到待办信息");
            }
            Long taskId = todo.getTaskId();
            return flowEngineService.getFlowInstanceTaskService().getById(taskId).map(task -> {
                String flowLinkCode = task.getFlowLinkCode();
                Long instanceId = task.getFlowInstanceId();

                TodoDetail todoDetail = TodoDetail.fromTodo(todo);
                todoDetail.setFlowInstanceId(instanceId);
                todoDetail.setLinkCode(flowLinkCode);

                return todoDetail;
            });
        });
    }

    public Future<Todo> getById(Long todoId) {
        return todoSQL.selectById(todoId).map(json -> {
            if (json == null) {
                return null;
            }
            return new Todo(json);
        });
    }

    public void setUidGeneratorService(UidGeneratorService uidGeneratorService) {
        this.uidGeneratorService = uidGeneratorService;
    }

    public void setTodoSQL(TodoSQL todoSQL) {
        this.todoSQL = todoSQL;
    }

    public void setFlowEngineService(FlowEngineService flowEngineService) {
        this.flowEngineService = flowEngineService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

}
