package com.whatswater.sql.dialect;


import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.table.*;
import com.whatswater.sql.utils.CollectionUtils;
import com.whatswater.sql.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public interface Dialect {
    SQL toSql(Update update);
    SQL toSql(Delete delete);
    SQL toSql(Table table);
    SQL toSql(Insert<?> insert);

    class SQL {
        private final StringBuilder sql;
        private final List<Object> params;

        public SQL() {
            this.sql = new StringBuilder();
            this.params = new ArrayList<>();
        }

        public SQL(StringBuilder sql) {
            this.sql = sql;
            this.params = new ArrayList<>();
        }

        public SQL(StringBuilder sql, List<Object> params) {
            this.sql = sql;
            this.params = params;
        }

        public SQL deleteLastChar(String character) {
            if (character.equals(sql.substring(sql.length() - 1, sql.length()))) {
                sql.deleteCharAt(sql.length() - 1);
            }
            return this;
        }

        public SQL append(String sql) {
            this.sql.append(sql);
            return this;
        }

        public SQL append(StringBuilder sql) {
            this.sql.append(sql);
            return this;
        }

        public SQL append(int v) {
            this.sql.append(v);
            return this;
        }

        public SQL append(long v) {
            this.sql.append(v);
            return this;
        }

        public SQL append(SQL sql) {
            this.sql.append(sql.getSql());
            if (CollectionUtils.isNotEmpty(sql.getParams())) {
                this.addParam(sql.getParams());
            }
            return this;
        }

        public void addParam(Object param) {
            this.params.add(param);
        }

        public void addParam(List<Object> params) {
            this.params.addAll(params);
        }

        public SQL removeBrackets() {
            if (StringUtils.startsWith(this.sql, "(") && StringUtils.endsWith(this.sql, ")")) {
                this.sql.deleteCharAt(0).deleteCharAt(this.sql.length() - 1);
            }
            return this;
        }

        public StringBuilder getSql() {
            return sql;
        }

        public List<Object> getParams() {
            return params;
        }

        public String getSqlValue() {
            return sql.toString();
        }
    }

    static void reLocationExpression(Query<?> query) {
        Table table = query.getTable();
        if (table instanceof SelectedTable) {

        } else if (table instanceof ComplexTable) {

        } else if (table instanceof JoinedTable) {

        }
    }
}
