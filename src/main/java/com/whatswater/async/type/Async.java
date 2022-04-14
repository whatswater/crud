package com.whatswater.async.type;


import io.vertx.core.Future;

public class Async {
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
        return t.result();
    }
}
