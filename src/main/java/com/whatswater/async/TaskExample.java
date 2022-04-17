package com.whatswater.async;


import com.whatswater.async.future.TaskFutureImpl;
import com.whatswater.async.handler.AwaitTaskHandler;

public class TaskExample implements Task {
    private AwaitTaskHandler _handler;
    private TaskFutureImpl _future;

    @Override
    public void moveToNext(int state) {
        try {
            switch (state) {
                case 0:
                case 1:
                    if (_handler.succeeded()) {
                        _handler.getResult();
                    } else {
                        throw _handler.getThrowable();
                    }
            }
        } catch (Throwable throwable) {
            _future.tryFail(throwable);
        }
    }
}
