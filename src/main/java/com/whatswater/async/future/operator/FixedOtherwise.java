package com.whatswater.async.future.operator;


import com.whatswater.async.future.TaskFutureImpl;
import io.vertx.core.impl.future.Listener;

public class FixedOtherwise<T> extends TaskFutureImpl<T> implements Listener<T> {
    private final T value;

    public FixedOtherwise(T value) {
        this.value = value;
    }

    @Override
    public void onSuccess(T value) {
        tryComplete(value);
    }

    @Override
    public void onFailure(Throwable failure) {
        tryComplete(value);
    }
}
