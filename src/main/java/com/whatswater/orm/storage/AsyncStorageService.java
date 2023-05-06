package com.whatswater.orm.storage;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.dsl.QueryDSL;
import com.whatswater.orm.data.id.DataId;

import java.util.List;

public interface AsyncStorageService {
    interface ActionHandler {
        void handle();
    }

    interface Handler<T> {
        void handle(T t);
    }

    void dispatchAction(Action action, ActionHandler handler);
    <Q> void list(QueryDSL<Q> query, Handler<List<Q>> handler);
    <Q> void getOne(QueryDSL<Q> query, Handler<Q> handler);
    void getById(DataId dataId, Handler<Object> handler);
}
