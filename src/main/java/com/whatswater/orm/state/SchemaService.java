package com.whatswater.orm.state;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.action.ActionContext;
import com.whatswater.orm.field.ComputeField;
import com.whatswater.orm.field.list.FieldList;
import com.whatswater.orm.operation.Operation;
import com.whatswater.orm.operation.OperationDelete;
import com.whatswater.orm.operation.OperationInsert;
import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.storage.AsyncStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class SchemaService implements SchemaListener {
    private DataStore dataStore;
    private Schema schema;

    private final ReadWriteLock consumerLock = new ReentrantReadWriteLock();
    private List<SchemaListener> consumerList;
    private AsyncStorageService storageService;

    public SchemaService(Schema schema, DataStore dataStore) {
        this.schema = schema;
        this.dataStore = dataStore;
    }

    @Override
    public void onAction(ActionContext context, Action action) {
        List<Operation> operations = action.operationList();

        for(Operation operation : operations) {
            if (operation instanceof OperationInsert) {

            } else if (operation instanceof OperationDelete) {

            }
        }
    }

    private void onInsert(ActionContext context, OperationInsert insert) {
        ComputeFieldUpdater updater = context.getOrCreateUpdater();

        Schema schema = insert.getSchema();
        // 从data中获取到当前数据的Id
        Object data = insert.getData();
        // 根据schema，获取监听此schema的field，并且触发field变更
//        DataId dataId = schema.getPrimaryKeyValue(data);
        FieldList fields = this.schema.fieldList();
        // 每种dirty的更新方式不同，如何处理不同种类的更新方式
        List<ComputeField> dirtyList = fields.computeBy(schema);
//        for(ComputeField computeField : dirtyList) {
//            // 此处需要计算出数据Id
//            updater.markDirty(this.schema, computeField, dataId);
//        }
    }

    public void add(ActionContext actionContext, Object data) {
        OperationInsert insert = new OperationInsert(schema, data);
        storageService.dispatchAction(insert, () -> notifyChange(actionContext, insert));
    }

    // 添加监听器
    public void addListener(SchemaListener listener) {
        Lock lock = consumerLock.writeLock();
        lock.lock();
        try {
            if (consumerList == null) {
                consumerList = new ArrayList<>();
            }
            consumerList.add(listener);
        } finally {
            lock.unlock();
        }
    }

    private void notifyChange(ActionContext actionContext, Action action) {
        withReadLock(() -> {
            if (consumerList == null) {
                return;
            }
            for(SchemaListener listener : consumerList) {
                listener.onAction(actionContext, action);
            }
        });
    }
    private void withReadLock(Runnable runnable) {
        Lock lock = consumerLock.readLock();
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }
}
