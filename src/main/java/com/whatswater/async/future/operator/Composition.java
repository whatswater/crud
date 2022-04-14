package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.Future;
import io.vertx.core.impl.future.FutureInternal;
import io.vertx.core.impl.future.Listener;

import java.util.function.Function;

public class Composition<T, U> extends TaskFutureImpl<U> implements Listener<T> {
    private final Function<T, Future<U>> successMapper;
    private final Function<Throwable, Future<U>> failureMapper;

    public Composition(Function<T, Future<U>> successMapper, Function<Throwable, Future<U>> failureMapper) {
        super();
        this.successMapper = successMapper;
        this.failureMapper = failureMapper;
    }

    @Override
    public void onSuccess(T value) {
        FutureInternal<U> future;
        try {
            future = (FutureInternal<U>) successMapper.apply(value);
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
            future = (FutureInternal<U>) failureMapper.apply(failure);
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
