package com.whatswater.async.type;


import io.vertx.core.Future;

import java.util.concurrent.*;

public class Async {
    public static ExecutorService executor = Executors.newFixedThreadPool(2);
    public static void init(ExecutorService executor) {
        Async.executor = executor;
    }
    public static void initExecutorService() {
        Async.executor = Executors.newFixedThreadPool(2);
    }
    public static final Future<Void> EMPTY_FUTURE = Future.succeededFuture();

    // 只能放在return前
    public static <T> Future<T> async() {
        return (Future<T>) EMPTY_FUTURE;
    }

    public static <T> Future<T> async(T t) {
        return Future.succeededFuture(t);
    }

    public interface AsyncFunction<T> {
        T apply();
    }

    public static <T> Future<T> async(AsyncFunction<T> t) {
        return Future.succeededFuture(t.apply());
    }

    public static <T> T await(Future<T> t) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        t.onComplete((ar) -> {
            if (ar.succeeded()) {
                completableFuture.complete(ar.result());
            } else {
                completableFuture.completeExceptionally(ar.cause());
            }
        });

        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
