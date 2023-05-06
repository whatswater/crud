package com.whatswater.orm.operation;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;


public class OperationInsert implements Operation, Action {
    private final Schema schema;
    private final Object data;
    private DataId dataId;

    public OperationInsert(Schema schema, Object data) {
        this.schema = schema;
        this.data = data;
    }

    public Schema getSchema() {
        return schema;
    }

    public Object getData() {
        return data;
    }

    public DataId getDataId() {
        return dataId;
    }

    public void setDataId(DataId dataId) {
        this.dataId = dataId;
    }

    @Override
    public OperationType operationType() {
        return OperationType.INSERT;
    }
}
