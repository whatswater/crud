package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.impl.future.Listener;

public class FixedMapping<T, U> extends TaskFutureImpl<U> implements Listener<T> {

    private final U value;

    public FixedMapping(U value) {
        this.value = value;
    }

    @Override
    public void onSuccess(T value) {
        tryComplete(this.value);
    }

    @Override
    public void onFailure(Throwable failure) {
        tryFail(failure);
    }
}

