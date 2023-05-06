package com.whatswater.orm.operation;

import com.whatswater.orm.dsl.UpdateDSL;

public class OperationUpdate implements Operation {
    final UpdateDSL dsl;
    int updateCount;

    public OperationUpdate(UpdateDSL dsl) {
        this.dsl = dsl;
    }

    public UpdateDSL getDsl() {
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
        return OperationType.UPDATE;
    }
}
