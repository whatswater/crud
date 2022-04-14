package com.whatswater.curd.project.module.todo;


import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

public interface ITodoAwaitService {
    Future<PageResult<Todo>> search(final Page page, TodoQuery query);

    Future<Todo> getByTaskId(long taskId);

    Future<String> generateString();

    void setTodoSQL(TodoSQL todoSQL);
}
