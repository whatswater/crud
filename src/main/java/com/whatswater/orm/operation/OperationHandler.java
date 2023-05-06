package com.whatswater.orm.operation;

public interface OperationHandler {
    void handleInsert(OperationInsert insert);
    void handleDelete(OperationDelete delete);
    void handleDeleteById(OperationDeleteById deleteById);
    void handleUpdate(OperationUpdate update);
    void handleUpdateById(OperationUpdateById updateById);

    default void handle(Operation operation) {
        OperationType type = operation.operationType();
        switch(type) {
            case INSERT:
                this.handleInsert((OperationInsert) operation);
                break;
            case UPDATE:
                this.handleUpdate((OperationUpdate) operation);
                break;
            case UPDATE_BY_ID:
                this.handleUpdateById((OperationUpdateById) operation);
                break;
            case DELETE:
                this.handleDelete((OperationDelete) operation);
                break;
            case DELETE_BY_ID:
                this.handleDeleteById((OperationDeleteById) operation);
                break;
        }
    }
}
