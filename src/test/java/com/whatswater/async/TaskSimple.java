package com.whatswater.async;


import com.whatswater.async.future.TaskFutureImpl;
import com.whatswater.async.handler.AwaitTaskHandler;
import io.vertx.core.Future;

public class TaskSimple implements Task {
    public AwaitCall local_0;
    public String local_1;
    public TaskFutureImpl _future = new TaskFutureImpl();
    public AwaitTaskHandler _handler;

    private static void set_local_0(AwaitCall var0, TaskSimple var1) {
        var1.local_0 = var0;
    }

    private static void set_local_1(String var0, TaskSimple var1) {
        var1.local_1 = var0;
    }

    private static void completeFuture(Object var0, TaskFutureImpl var1) {
        var1.tryComplete(var0);
    }

    public TaskSimple() {
    }

    @Override
    public void moveToNext(int var1) {
        switch(var1) {
            case 0:
                Future var10000 = AwaitCall.newFuture();
                this._handler = new AwaitTaskHandler(this, 1);
                var10000.onComplete(this._handler);
                return;
            case 1:
                set_local_1((String)this._handler.getResult(), this);
                completeFuture(this.local_1 + "1234", this._future);
                return;
            default:
                this._future.tryFail(this._handler.getThrowable());
        }
    }
}
