package com.whatswater.async.future;


import com.whatswater.async.future.operator.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.future.FutureInternal;
import io.vertx.core.impl.future.Listener;

import java.util.Objects;
import java.util.function.Function;

public abstract class TaskFutureBase<T> implements FutureInternal<T> {
    @Override
    public ContextInternal context() {
        return null;
    }


    protected final void emitSuccess(T value, Listener<T> listener) {
        listener.onSuccess(value);
    }

    protected final void emitFailure(Throwable cause, Listener<T> listener) {
        listener.onFailure(cause);
    }

    @Override
    public <U> Future<U> compose(Function<T, Future<U>> successMapper, Function<Throwable, Future<U>> failureMapper) {
        Objects.requireNonNull(successMapper, "No null success mapper accepted");
        Objects.requireNonNull(failureMapper, "No null failure mapper accepted");
        Composition<T, U> operation = new Composition<>(successMapper, failureMapper);
        addListener(operation);
        return operation;
    }

    @Override
    public <U> Future<U> transform(Function<AsyncResult<T>, Future<U>> mapper) {
        Objects.requireNonNull(mapper, "No null mapper accepted");
        Transformation<T, U> operation = new Transformation<>(this, mapper);
        addListener(operation);
        return operation;
    }

    @Override
    public <U> Future<T> eventually(Function<Void, Future<U>> mapper) {
        Objects.requireNonNull(mapper, "No null mapper accepted");
        Eventually<T, U> operation = new Eventually<>(mapper);
        addListener(operation);
        return operation;
    }

    @Override
    public <U> Future<U> map(Function<T, U> mapper) {
        Objects.requireNonNull(mapper, "No null mapper accepted");
        Mapping<T, U> operation = new Mapping<>(mapper);
        addListener(operation);
        return operation;
    }

    @Override
    public <V> Future<V> map(V value) {
        FixedMapping<T, V> transformation = new FixedMapping<>(value);
        addListener(transformation);
        return transformation;
    }

    @Override
    public Future<T> otherwise(Function<Throwable, T> mapper) {
        Objects.requireNonNull(mapper, "No null mapper accepted");
        Otherwise<T> transformation = new Otherwise<>(mapper);
        addListener(transformation);
        return transformation;
    }

    @Override
    public Future<T> otherwise(T value) {
        FixedOtherwise<T> operation = new FixedOtherwise<>(value);
        addListener(operation);
        return operation;
    }
}
