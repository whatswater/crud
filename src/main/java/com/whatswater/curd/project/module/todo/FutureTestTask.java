package com.whatswater.curd.project.module.todo;

import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.Future;

public class FutureTestTask {
    public static Future<String> newFuture(final int i) {
        TaskFutureImpl<String> taskFuture = new TaskFutureImpl<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    taskFuture.tryFail(e);
                }
                taskFuture.tryComplete("12345" + i);
            }
        });
        thread.start();
        return taskFuture;
    }
}
