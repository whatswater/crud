package com.whatswater.curd.project.module.workflow.flowInstanceVariable;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstance;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SQLExecute;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowInstanceVariableService {
    private final FlowInstanceVariableSQL flowInstanceVariableSQL;

    public FlowInstanceVariableService(MySQLPool pool) {
        this.flowInstanceVariableSQL = new FlowInstanceVariableSQL(SQLExecute.createMySQL(pool));
    }

    public Future<PageResult<FlowInstanceVariable>> search(Page page, FlowInstanceVariableQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return flowInstanceVariableSQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return flowInstanceVariableSQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(FlowInstanceVariable::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<FlowInstanceVariable> getById(Long flowInstanceVariableId) {
        Future<JsonObject> result = flowInstanceVariableSQL.selectById(flowInstanceVariableId);
        return result.map(json -> {
            if (json == null) {
                return null;
            }
            return new FlowInstanceVariable(json);
        });
    }

    public Future<Long> insert(FlowInstanceVariable flowInstanceVariable) {
        return flowInstanceVariableSQL.insertNonEmptyGeneratedKeys(flowInstanceVariable, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(FlowInstanceVariable flowInstanceVariable) {
        return flowInstanceVariableSQL.updateNonEmptyById(flowInstanceVariable);
    }

    public Future<FlowInstance> withVariable(final FlowInstance flowInstance) {
        return this.queryFlowInstanceVariable(flowInstance.getId()).map(flowInstanceVariables -> {
            Map<String, FlowInstanceVariable> variableMap = flowInstanceVariables
                .stream()
                .collect(Collectors.toMap(FlowInstanceVariable::getVariableName, Function.identity(), (a1, a2) -> a1));
            flowInstance.setVariableTable(variableMap);
            return flowInstance;
        });
    }

    /**
     * 根据流程实例Id和变量名获取变量
     * @param flowInstanceId 流程实例变量
     * @param variableName 变量名
     * @return
     */
    public Future<FlowInstanceVariable> getByFlowInstanceIdAndName(long flowInstanceId, String variableName) {
        SqlAssist sqlAssist = FlowInstanceVariable.instanceIdVariableNameSqlAssist(flowInstanceId, variableName);
        return flowInstanceVariableSQL.selectAll(sqlAssist).map(list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            return new FlowInstanceVariable(list.get(0));
        });
    }

    /**
     * 查询流程实例变量
     * @param flowInstanceId 流程实例变量
     * @return 流程实例变量列表
     */
    public Future<List<FlowInstanceVariable>> queryFlowInstanceVariable(long flowInstanceId) {
        SqlAssist sqlAssist = FlowInstanceVariable.instanceIdSqlAssist(flowInstanceId);
        return flowInstanceVariableSQL.selectAll(sqlAssist).map(list -> {
            if (list == null) {
                return Collections.emptyList();
            }
            return list.stream().map(FlowInstanceVariable::new).collect(Collectors.toList());
        });
    }

    /**
     * 设置流程实例变量
     * @param flowInstanceId 流程实例Id
     * @param variableName 变量名
     * @param value 变量值
     * @return 流程实例变量对象
     */
    public Future<FlowInstanceVariable> setFlowInstanceVariable(long flowInstanceId, String variableName, String value) {
        return getByFlowInstanceIdAndName(flowInstanceId, variableName).compose(old -> {
            FlowInstanceVariable flowInstanceVariable = new FlowInstanceVariable();
            flowInstanceVariable.setFlowInstanceId(flowInstanceId);
            flowInstanceVariable.setVariableName(variableName);
            flowInstanceVariable.setVariableValue(value);
            flowInstanceVariable.setCreateTime(LocalDateTime.now());

            if (old == null) {
                return insert(flowInstanceVariable).map(id -> {
                    flowInstanceVariable.setId(id);
                    return flowInstanceVariable;
                });
            } else {
                return update(flowInstanceVariable).map(cnt -> {
                    flowInstanceVariable.setId(old.getId());
                    return flowInstanceVariable;
                });
            }
        });
    }

    /**
     * 初始化流程实例变量
     * @param flowInstanceId 流程实例Id
     * @param variableMap 初始变量表
     * @return 流程变量列表
     */
    public Future<List<FlowInstanceVariable>> initFlowInstanceVariable(long flowInstanceId, Map<String, String> variableMap) {
        final LocalDateTime createTime = LocalDateTime.now();
        final List<SameFutureBuilder<Long>> sameFutureBuilders = new ArrayList<>(variableMap.size());
        final List<FlowInstanceVariable> ret = new ArrayList<>(variableMap.size());
        for (Map.Entry<String, String> entry: variableMap.entrySet()) {
            final FlowInstanceVariable flowInstanceVariable = new FlowInstanceVariable();
            flowInstanceVariable.setFlowInstanceId(flowInstanceId);
            flowInstanceVariable.setVariableName(entry.getKey());
            flowInstanceVariable.setVariableValue(entry.getValue());
            flowInstanceVariable.setCreateTime(createTime);
            ret.add(flowInstanceVariable);

            sameFutureBuilders.add(idList -> this.insert(flowInstanceVariable));
        }

        return CrudUtils.serialTask(sameFutureBuilders).map(idList -> {
            for (int i = 0; i < ret.size(); i++) {
                ret.get(i).setId(idList.get(i));
            }
            return ret;
        });
    }
}
