package com.whatswater.sql.dialect;


import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;

import java.util.List;

public interface Dialect {
    SqlAndParam toSql(Update update);
    SqlAndParam toSql(Delete delete);
    SqlAndParam toSql(Query<?> query);
    SqlAndParam toSql(Insert<?> insert);

    class SqlAndParam {
        private String sql;
        private List<Object> params;

        public SqlAndParam(String sql) {
            this.sql = sql;
        }

        public SqlAndParam(String sql, List<Object> params) {
            this.sql = sql;
            this.params = params;
        }

        public String getSql() {
            return sql;
        }

        public List<Object> getParams() {
            return params;
        }
    }
}
