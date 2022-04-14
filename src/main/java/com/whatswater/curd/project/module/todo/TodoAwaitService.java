package com.whatswater.curd.project.module.todo;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;

import java.util.List;
import java.util.stream.Collectors;

import static com.whatswater.async.type.Async.async;
import static com.whatswater.async.type.Async.await;

public class TodoAwaitService implements ITodoAwaitService {
    public TodoSQL todoSQL;

    public TodoAwaitService() {
    }

    @Override
    public Future<PageResult<Todo>> search(final Page page, TodoQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        Long total = await(todoSQL.getCount(sqlAssist));
        if (CrudUtils.notZero(total)) {
            List<JsonObject> jsonList = await(todoSQL.selectAll(sqlAssist));
            List<Todo> todoList = jsonList.stream().map(Todo::new).collect(Collectors.toList());
            return async(PageResult.of(todoList, page, total));
        }
        return async(PageResult.empty());
    }

    @Override
    public Future<Todo> getByTaskId(long taskId) {
        SqlAssist sqlAssist = Todo.taskIdSqlAssist(taskId);

        List<JsonObject> jsonList = await(todoSQL.selectAll(sqlAssist));
        if (CollectionUtil.isEmpty(jsonList)) {
            return async();
        }
        return async(new Todo(jsonList.get(0)));
    }

    @Override
    public Future<String> generateString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(await(FutureTestTask.newFuture(i)));
        }
        return async(sb.toString());
    }

    @Override
    public void setTodoSQL(TodoSQL todoSQL) {
        this.todoSQL = todoSQL;
    }
}
