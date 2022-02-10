package com.whatswater.curd.project.sys.employeeFilter;


import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeFilterDataService {
    private EmployeeFilterDataSQL employeeFilterDataSQL;
    public EmployeeFilterDataService() {

    }

    public Future<List<EmployeeFilterData>> queryByCode(String code) {
        SqlAssist sqlAssist = EmployeeFilterData.codeSqlAssist(code);

        return employeeFilterDataSQL.selectAll(sqlAssist).map(jsonList -> {
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }
            return jsonList.stream().map(EmployeeFilterData::new).collect(Collectors.toList());
        });
    }

    public Future<List<Long>> batchInsert(List<EmployeeFilterData> dataList) {
        List<SameFutureBuilder<Long>> taskList = dataList.stream().map(filter -> {
            return (SameFutureBuilder<Long>) r -> this.insert(filter);
        }).collect(Collectors.toList());
        return CrudUtils.serialTask(taskList);
    }

    public Future<Integer> deleteByCode(String code) {
        SqlAssist sqlAssist = EmployeeFilterData.codeSqlAssist(code);
        return employeeFilterDataSQL.deleteByAssist(sqlAssist);
    }

    public Future<Long> insert(EmployeeFilterData employeeFilterData) {
        return employeeFilterDataSQL.insertNonEmptyGeneratedKeys(employeeFilterData, MySQLClient.LAST_INSERTED_ID);
    }

    public Future<Integer> update(EmployeeFilterData employeeFilterData) {
        return employeeFilterDataSQL.updateNonEmptyById(employeeFilterData);
    }

    public void setEmployeeFilterDataSQL(EmployeeFilterDataSQL employeeFilterDataSQL) {
        this.employeeFilterDataSQL = employeeFilterDataSQL;
    }
}
