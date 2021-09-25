package com.whatswater.sql.statement;


import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.table.Table;

import java.util.List;


public class Query<T> {
    private Table table;
    private List<SelectColumn> selectList;
    private boolean distinct;
    Limit limit;
    private ResultMapper<T> resultMapper;

    public Query() {

    }

    public Query(Table table, ResultMapper<T> resultMapper) {
        this.table = table;
        this.resultMapper = resultMapper;
    }

    public Query(Table table, ResultMapper<T> resultMapper, Limit limit) {
        this.table = table;
        this.resultMapper = resultMapper;
    }

    public Query(Table table, ResultMapper<T> resultMapper, Limit limit, boolean distinct) {
        this.table = table;
        this.resultMapper = resultMapper;
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public ResultMapper<T> getResultMapper() {
        return resultMapper;
    }

    public void setResultMapper(ResultMapper<T> resultMapper) {
        this.resultMapper = resultMapper;
    }
}
