package com.whatswater.orm.storage;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.dsl.QueryDSL;
import com.whatswater.orm.operation.Operation;
import com.whatswater.orm.operation.OperationDelete;
import com.whatswater.orm.operation.OperationDeleteById;
import com.whatswater.orm.operation.OperationHandler;
import com.whatswater.orm.operation.OperationInsert;
import com.whatswater.orm.operation.OperationUpdate;
import com.whatswater.orm.operation.OperationUpdateById;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class MysqlStorageService implements AsyncStorageService {
    static class MysqlDirective {

    }

    static class MysqlOperationMerger implements OperationHandler {
        List<MysqlDirective> directiveList;
        Operation prevOperation;

        public void merge(Operation operation) {

        }

        @Override
        public void handleInsert(OperationInsert insert) {

        }

        @Override
        public void handleDelete(OperationDelete delete) {

        }

        @Override
        public void handleDeleteById(OperationDeleteById deleteById) {

        }

        @Override
        public void handleUpdate(OperationUpdate update) {

        }

        @Override
        public void handleUpdateById(OperationUpdateById updateById) {

        }
    }

    DataSource ds;
    // merge方法的返回值不是operation类型
    private List<MysqlDirective> mergeOperation(List<Operation> operations) {
        List<MysqlDirective> directiveList = new ArrayList<>();
        return directiveList;
    }

    @Override
    public void dispatchAction(Action action, ActionHandler handler) {
        List<Operation> operationList = action.operationList();
        if (operationList == null || operationList.isEmpty()) {
            return;
        }
        List<MysqlDirective> mergeOperationList = mergeOperation(operationList);
        for(MysqlDirective directive : mergeOperationList) {
            // 处理指令
        }
    }

    @Override
    public <Q> void list(QueryDSL<Q> query, Handler<List<Q>> handler) {
        // 根据query构建table
        // storage实现table转sql的程序
        // resultMapper获取值
        // 事务管理器获取当前mysql连接
        // 将值设置到handler中
    }

    @Override
    public <Q> void getOne(QueryDSL<Q> query, Handler<Q> handler) {

    }

    @Override
    public void getById(DataId dataId, Handler<Object> handler) {

    }
}
