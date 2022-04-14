package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.impl.future.Listener;

import java.util.function.Function;

public class Otherwise<T> extends TaskFutureImpl<T> implements Listener<T> {

    private final Function<Throwable, T> mapper;

    public Otherwise(Function<Throwable, T> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onSuccess(T value) {
        tryComplete(value);
    }

    @Override
    public void onFailure(Throwable failure) {
        T result;
        try {
            result = mapper.apply(failure);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        tryComplete(result);
    }
}

