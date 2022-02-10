package com.whatswater.curd.project.module.todo;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.module.workflow.event.GenerateTaskContext;
import com.whatswater.curd.project.module.workflow.event.UpdateTaskStatusContext;
import com.zandero.rest.annotation.Post;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/business/todo")
public class TodoRest {
    private final TodoService todoService;

    public TodoRest(TodoService todoService) {
        this.todoService = todoService;
    }

    // 从workflow来的任务，任务状态的变化也通过此接口
    @Post
    @Path("/fromWorkflow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Todo>> fromWorkflow(Todo todo) {
        return todoService.insert(todo).map(RestResult::success);
    }

    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<PageResult<Todo>>> search(@BeanParam Page page, TodoQuery query) {
        if (query == null) {
            query = new TodoQuery();
        }
        return todoService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/getTodoDetail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<TodoDetail>> getTodoDetail(@QueryParam("id") Long todoId) {
        Assert.assertNotNull(todoId, "待办Id不能为空");
        return todoService.getTodoDetail(todoId).map(RestResult::success);
    }

    @Post
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Todo>> insert(Todo todo) {
        return todoService.insert(todo).map(RestResult::success);
    }

    @Post
    @Path("/onTaskCreated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<Todo>>> onTaskCreated(GenerateTaskContext context) {
        return todoService.onTaskCreated(context).map(RestResult::success);
    }

    @Post
    @Path("/onTaskCompleted")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> complete(UpdateTaskStatusContext updateTaskStatusContext) {
        return todoService.completeTodo(updateTaskStatusContext.getTaskId()).map(RestResult::success);
    }

    @Post
    @Path("/onTaskCanceled")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> cancel(UpdateTaskStatusContext updateTaskStatusContext) {
        return todoService.cancelTodo(updateTaskStatusContext.getTaskId()).map(RestResult::success);
    }

    @Post
    @Path("/read")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> read(@QueryParam("id") long todoId) {
        return todoService.readTodo(todoId).map(RestResult::success);
    }
}
