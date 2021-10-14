package com.whatswater.sql.dialect;


import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.table.*;

import java.util.List;

public interface Dialect {
    SQL toSql(Update update);
    SQL toSql(Delete delete);
    SQL toSql(Query<?> query);
    SQL toSql(Insert<?> insert);

    class SQL {
        private final String sql;
        private List<Object> params;

        public SQL(String sql) {
            this.sql = sql;
        }

        public SQL(String sql, List<Object> params) {
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

    static void reLocationExpression(Query<?> query) {
        Table table = query.getTable();
        if (table instanceof SelectedTable) {

        } else if (table instanceof ComplexTable) {

        } else if (table instanceof JoinedTable) {

        }
    }
}
