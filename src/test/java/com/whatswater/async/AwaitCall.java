package com.whatswater.async;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.Future;

import static com.whatswater.async.type.Async.async;
import static com.whatswater.async.type.Async.await;

public class AwaitCall {
    public static Future<String> newFuture() {
        TaskFutureImpl<String> taskFuture = new TaskFutureImpl<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    taskFuture.tryFail(e);
                }
                taskFuture.tryComplete("12345");
            }
        });
        thread.start();
        return taskFuture;
    }

//    public Future<Void> simpleTest() {
//
//        return async();
//    }

    public Future<String> simpleTest() {
        String d1 = await(newFuture());
        return async(d1 + "1234");
    }

//    public Future<String> simpleTest() {
//        StringBuilder result = new StringBuilder();
//        for (int i = 0; i < 10; i++) {
//            String r = await(newFuture());
//            result.append(r);
//        }
//        result.append(await(newFuture()));
//        return async(result.toString());
//    }
}
