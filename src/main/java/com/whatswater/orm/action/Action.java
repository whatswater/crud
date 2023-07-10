package com.whatswater.orm.action;

import com.whatswater.orm.operation.Operation;

import java.util.List;

// 除operation外，应当存在其他的东西，表示operation的先决条件，例如当不存在时才能insert
public interface Action {
    List<Operation> operationList();
}
