package com.whatswater.curd;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Transaction;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class TransactionFuture<T> implements Future<T> {
    private Future<T> innerFuture;
    private Transaction transaction;

    @Override
    public boolean isComplete() {
        return innerFuture.isComplete();
    }

    @Override
    public Future<T> onComplete(Handler<AsyncResult<T>> handler) {
        Future<T> future = innerFuture.onComplete(handler);
        return newFuture(future);
    }

    @Override
    public Future<T> onSuccess(Handler<T> handler) {
        Future<T> future = innerFuture.onSuccess(handler);
        return newFuture(future);
    }

    @Override
    public Future<T> onFailure(Handler<Throwable> handler) {
        return newFuture(innerFuture.onFailure(handler));
    }

    @Override
    public T result() {
        return innerFuture.result();
    }

    @Override
    public Throwable cause() {
        return innerFuture.cause();
    }

    @Override
    public boolean succeeded() {
        return innerFuture.succeeded();
    }

    @Override
    public boolean failed() {
        return innerFuture.failed();
    }

    @Override
    public <U> Future<U> flatMap(Function<T, Future<U>> mapper) {
        return newFuture(innerFuture.flatMap(mapper));
    }

    @Override
    public <U> Future<U> compose(Function<T, Future<U>> mapper) {
        return newFuture(innerFuture.compose(mapper));
    }

    @Override
    public Future<T> recover(Function<Throwable, Future<T>> mapper) {
        return newFuture(innerFuture.recover(mapper));
    }

    @Override
    public <U> Future<U> compose(Function<T, Future<U>> successMapper, Function<Throwable, Future<U>> failureMapper) {
        return newFuture(innerFuture.compose(successMapper, failureMapper));
    }

    @Override
    public <U> Future<U> transform(Function<AsyncResult<T>, Future<U>> mapper) {
        return newFuture(innerFuture.transform(mapper));
    }

    @Override
    public <U> Future<T> eventually(Function<Void, Future<U>> mapper) {
        return newFuture(innerFuture.eventually(mapper));
    }

    @Override
    public <U> Future<U> map(Function<T, U> mapper) {
        return newFuture(innerFuture.map(mapper));
    }

    @Override
    public <V> Future<V> map(V value) {
        return newFuture(innerFuture.map(value));
    }

    @Override
    public <V> Future<V> mapEmpty() {
        return newFuture(innerFuture.mapEmpty());
    }

    @Override
    public Future<T> otherwise(Function<Throwable, T> mapper) {
        return newFuture(innerFuture.otherwise(mapper));
    }

    @Override
    public Future<T> otherwise(T value) {
        return newFuture(innerFuture.otherwise(value));
    }

    @Override
    public Future<T> otherwiseEmpty() {
        return newFuture(innerFuture.otherwiseEmpty());
    }

    @Override
    public CompletionStage<T> toCompletionStage() {
        return innerFuture.toCompletionStage();
    }

    <U> TransactionFuture<U> newFuture(Future<U> future) {
        TransactionFuture<U> ret = new TransactionFuture<>();
        ret.innerFuture = future;
        ret.transaction = this.transaction;
        return ret;
    }
}
