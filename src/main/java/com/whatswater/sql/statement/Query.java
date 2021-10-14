package com.whatswater.sql.statement;


import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.table.Table;

import java.util.List;


public class Query<T> {
    private Table table;
    private ResultMapper<T> resultMapper;

    public Query(Table table, ResultMapper<T> resultMapper) {
        this.table = table;
        this.resultMapper = resultMapper;
    }

    public Table getTable() {
        return table;
    }

    public ResultMapper<T> getResultMapper() {
        return resultMapper;
    }

    public void setResultMapper(ResultMapper<T> resultMapper) {
        this.resultMapper = resultMapper;
    }
}
