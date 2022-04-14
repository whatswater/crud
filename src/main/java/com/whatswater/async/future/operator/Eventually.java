package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.Future;
import io.vertx.core.impl.future.FutureInternal;
import io.vertx.core.impl.future.Listener;

import java.util.function.Function;

public class Eventually<T, U> extends TaskFutureImpl<T> implements Listener<T> {
    private final Function<Void, Future<U>> mapper;

    public Eventually(Function<Void, Future<U>> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onSuccess(T value) {
        FutureInternal<U> future;
        try {
            future = (FutureInternal<U>) mapper.apply(null);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        future.addListener(new Listener<U>() {
            @Override
            public void onSuccess(U ignore) {
                tryComplete(value);
            }
            @Override
            public void onFailure(Throwable ignore) {
                tryComplete(value);
            }
        });
    }

    @Override
    public void onFailure(Throwable failure) {
        FutureInternal<U> future;
        try {
            future = (FutureInternal<U>) mapper.apply(null);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        future.addListener(new Listener<U>() {
            @Override
            public void onSuccess(U ignore) {
                tryFail(failure);
            }
            @Override
            public void onFailure(Throwable ignore) {
                tryFail(failure);
            }
        });
    }
}
