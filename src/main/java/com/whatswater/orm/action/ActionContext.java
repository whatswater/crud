package com.whatswater.orm.action;

import com.whatswater.orm.state.ComputeFieldUpdater;
import com.whatswater.orm.util.MetaKey;

public interface ActionContext {
    <T> void put(MetaKey<T> key, T value);
    <T> T get(MetaKey<T> key);
    void logAction(Action action);
    ComputeFieldUpdater getOrCreateUpdater();
}
