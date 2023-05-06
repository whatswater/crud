package com.whatswater.orm.operation;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;

public class OperationDeleteById implements Operation {
    private final Schema schema;
    private final DataId dataId;
    private int updateCount = 0;

    public OperationDeleteById(Schema schema, DataId dataId) {
        this.schema = schema;
        this.dataId = dataId;
    }

    public Schema getSchema() {
        return schema;
    }

    public DataId getDataId() {
        return dataId;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public OperationType operationType() {
        return OperationType.DELETE_BY_ID;
    }
}
