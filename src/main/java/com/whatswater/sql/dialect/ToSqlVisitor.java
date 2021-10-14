package com.whatswater.sql.dialect;


import java.util.ArrayList;
import java.util.List;

public class ToSqlVisitor {
    protected StringBuilder sql;
    protected List<Object> params;

    public ToSqlVisitor() {
        this.sql = new StringBuilder();
        this.params = new ArrayList<>();
    }

    public ToSqlVisitor(List<Object> params) {
        this.sql = new StringBuilder();
        this.params = params;
    }

    public ToSqlVisitor(StringBuilder sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public void clearAll() {
        this.sql = new StringBuilder();
        this.params = new ArrayList<>();
    }

    public void clearSql() {
        this.sql = new StringBuilder();
    }

    public StringBuilder getSql() {
        return sql;
    }

    public StringBuilder getAndClearSql() {
        StringBuilder originSql = sql;
        this.sql = new StringBuilder();
        return originSql;
    }

    public List<Object> getParams() {
        return params;
    }
}
