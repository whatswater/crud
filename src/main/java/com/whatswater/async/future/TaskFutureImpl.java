package com.whatswater.async.future;


import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.impl.future.Listener;

import java.util.ArrayList;
import java.util.Objects;

public class TaskFutureImpl<T> extends TaskFutureBase<T> {
    private static final Object NULL_VALUE = new Object();

    private Object value;
    private Listener<T> listener;

    public TaskFutureImpl() {
        super();
    }

    /**
     * The result of the operation. This will be null if the operation failed.
     */
    public synchronized T result() {
        return value instanceof TaskFutureImpl.CauseHolder ? null : value == NULL_VALUE ? null : (T) value;
    }

    /**
     * An exception describing failure. This will be null if the operation succeeded.
     */
    public synchronized Throwable cause() {
        return value instanceof TaskFutureImpl.CauseHolder ? ((TaskFutureImpl.CauseHolder)value).cause : null;
    }

    /**
     * Did it succeed?
     */
    public synchronized boolean succeeded() {
        return value != null && !(value instanceof TaskFutureImpl.CauseHolder);
    }

    /**
     * Did it fail?
     */
    public synchronized boolean failed() {
        return value instanceof TaskFutureImpl.CauseHolder;
    }

    /**
     * Has it completed?
     */
    public synchronized boolean isComplete() {
        return value != null;
    }

    @Override
    public Future<T> onSuccess(Handler<T> handler) {
        Objects.requireNonNull(handler, "No null handler accepted");
        addListener(new Listener<T>() {
            @Override
            public void onSuccess(T value) {
                try {
                    handler.handle(value);
                } catch (Throwable t) {
                    throw t;
                }
            }
            @Override
            public void onFailure(Throwable failure) {
            }
        });
        return this;
    }

    @Override
    public Future<T> onFailure(Handler<Throwable> handler) {
        Objects.requireNonNull(handler, "No null handler accepted");
        addListener(new Listener<T>() {
            @Override
            public void onSuccess(T value) {
            }
            @Override
            public void onFailure(Throwable failure) {
                try {
                    handler.handle(failure);
                } catch (Throwable t) {
                    throw t;
                }
            }
        });
        return this;
    }

    @Override
    public Future<T> onComplete(Handler<AsyncResult<T>> handler) {
        Objects.requireNonNull(handler, "No null handler accepted");
        Listener<T> listener;
        if (handler instanceof Listener) {
            listener = (Listener<T>) handler;
        } else {
            listener = new Listener<T>() {
                @Override
                public void onSuccess(T value) {
                    try {
                        handler.handle(TaskFutureImpl.this);
                    } catch (Throwable t) {
                        throw t;
                    }
                }
                @Override
                public void onFailure(Throwable failure) {
                    try {
                        handler.handle(TaskFutureImpl.this);
                    } catch (Throwable t) {
                        throw t;
                    }
                }
            };
        }
        addListener(listener);
        return this;
    }

    @Override
    public void addListener(Listener<T> listener) {
        Object v;
        synchronized (this) {
            v = value;
            if (v == null) {
                if (this.listener == null) {
                    this.listener = listener;
                } else {
                    TaskFutureImpl.ListenerArray<T> listeners;
                    if (this.listener instanceof TaskFutureImpl.ListenerArray) {
                        listeners = (TaskFutureImpl.ListenerArray<T>) this.listener;
                    } else {
                        listeners = new TaskFutureImpl.ListenerArray<>();
                        listeners.add(this.listener);
                        this.listener = listeners;
                    }
                    listeners.add(listener);
                }
                return;
            }
        }
        if (v instanceof TaskFutureImpl.CauseHolder) {
            emitFailure(((TaskFutureImpl.CauseHolder)v).cause, listener);
        } else {
            if (v == NULL_VALUE) {
                v = null;
            }
            emitSuccess((T) v, listener);
        }
    }

    public boolean tryComplete(T result) {
        Listener<T> l;
        synchronized (this) {
            if (value != null) {
                return false;
            }
            value = result == null ? NULL_VALUE : result;
            l = listener;
            listener = null;
        }
        if (l != null) {
            emitSuccess(result, l);
        }
        return true;
    }

    public boolean tryFail(Throwable cause) {
        if (cause == null) {
            cause = new NoStackTraceThrowable(null);
        }
        Listener<T> l;
        synchronized (this) {
            if (value != null) {
                return false;
            }
            value = new TaskFutureImpl.CauseHolder(cause);
            l = listener;
            listener = null;
        }
        if (l != null) {
            emitFailure(cause, l);
        }
        return true;
    }

    @Override
    public String toString() {
        synchronized (this) {
            if (value instanceof TaskFutureImpl.CauseHolder) {
                return "Future{cause=" + ((TaskFutureImpl.CauseHolder)value).cause.getMessage() + "}";
            }
            if (value != null) {
                if (value == NULL_VALUE) {
                    return "Future{result=null}";
                }
                StringBuilder sb = new StringBuilder("Future{result=");
                formatValue(value, sb);
                sb.append("}");
                return sb.toString();
            }
            return "Future{unresolved}";
        }
    }

    protected void formatValue(Object value, StringBuilder sb) {
        sb.append(value);
    }

    private static class ListenerArray<T> extends ArrayList<Listener<T>> implements Listener<T> {
        @Override
        public void onSuccess(T value) {
            for (Listener<T> handler : this) {
                handler.onSuccess(value);
            }
        }
        @Override
        public void onFailure(Throwable failure) {
            for (Listener<T> handler : this) {
                handler.onFailure(failure);
            }
        }
    }

    private static class CauseHolder {

        private final Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }
}
