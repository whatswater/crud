package com.whatswater.orm.operation;

import com.whatswater.orm.dsl.DeleteDSL;

public class OperationDelete implements Operation {
    final DeleteDSL dsl;
    int updateCount;

    public OperationDelete(DeleteDSL dsl) {
        this.dsl = dsl;
    }

    public DeleteDSL getDsl() {
        return dsl;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public OperationType operationType() {
        return OperationType.DELETE;
    }
}
