package com.whatswater.async;


public interface Task {
    int EXCEPTION_STATE = -1;

    void moveToNext(int state);
}
