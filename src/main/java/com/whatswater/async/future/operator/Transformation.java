package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.impl.future.FutureInternal;
import io.vertx.core.impl.future.Listener;

import java.util.function.Function;


public class Transformation<T, U> extends TaskFutureImpl<U> implements Listener<T> {
    private final Future<T> future;
    private final Function<AsyncResult<T>, Future<U>> mapper;

    public Transformation(Future<T> future, Function<AsyncResult<T>, Future<U>> mapper) {
        this.future = future;
        this.mapper = mapper;
    }

    @Override
    public void onSuccess(T value) {
        FutureInternal<U> future;
        try {
            future = (FutureInternal<U>) mapper.apply(this.future);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        future.addListener(newListener());
    }

    @Override
    public void onFailure(Throwable failure) {
        FutureInternal<U> future;
        try {
            future = (FutureInternal<U>) mapper.apply(this.future);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        future.addListener(newListener());
    }

    private Listener<U> newListener() {
        return new Listener<U>() {
            @Override
            public void onSuccess(U value) {
                tryComplete(value);
            }
            @Override
            public void onFailure(Throwable failure) {
                tryFail(failure);
            }
        };
    }
}

