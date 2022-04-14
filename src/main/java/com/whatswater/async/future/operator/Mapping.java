package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.impl.future.Listener;

import java.util.function.Function;

public class Mapping<T, U> extends TaskFutureImpl<U> implements Listener<T> {
    private final Function<T, U> successMapper;

    public Mapping(Function<T, U> successMapper) {
        this.successMapper = successMapper;
    }

    @Override
    public void onSuccess(T value) {
        U result;
        try {
            result = successMapper.apply(value);
        } catch (Throwable e) {
            tryFail(e);
            return;
        }
        tryComplete(result);
    }

    @Override
    public void onFailure(Throwable failure) {
        tryFail(failure);
    }
}

