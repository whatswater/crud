package com.whatswater.orm.action;

import com.whatswater.orm.operation.Operation;

import java.util.List;

public interface Action {
    List<Operation> operationList();
}
