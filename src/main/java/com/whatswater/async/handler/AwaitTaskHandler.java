package com.whatswater.async.handler;


import com.whatswater.async.Task;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class AwaitTaskHandler<T> implements Handler<AsyncResult<T>> {
    private Task task;
    private int position;
    private AsyncResult<T> result;

    public AwaitTaskHandler(Task task, int position) {
        this.task = task;
        this.position = position;
    }

    @Override
    public void handle(AsyncResult<T> result) {
        this.result = result;
        this.task.moveToNext(result.succeeded() ? this.position : Task.EXCEPTION_STATE);
    }

    public T getResult() {
        return result.result();
    }

    public Throwable getThrowable() {
        return result.cause();
    }
}
