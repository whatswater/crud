package com.whatswater.orm.operation;

import com.whatswater.orm.action.Action;

import java.util.Collections;
import java.util.List;

public interface Operation extends Action {
    default List<Operation> operationList() {
        return Collections.singletonList(this);
    }

    OperationType operationType();
}
