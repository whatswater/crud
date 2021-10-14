package com.whatswater.sql.statement;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.JdbcParameter;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinType;
import com.whatswater.sql.table.Table;

import java.util.ArrayList;
import java.util.List;

public class Update {
    private List<UpdateColumn> valueSetList;
    private Table table;
    private final DbTable<?> dbTable;
    private BoolExpression where;
    private Limit limit;

    public Update(DbTable<?> dbTable) {
        this.dbTable = dbTable;
        this.table = dbTable;
    }

    public Update set(RawColumnReference sqlColumn, Expression value) {
        if (!dbTable.equals(sqlColumn.getTable())) {
            throw new RuntimeException("X3");
        }

        UpdateColumn statement = new UpdateColumn(sqlColumn, value);
        if (valueSetList == null) {
            valueSetList = new ArrayList<>();
        }
        valueSetList.add(statement);
        return this;
    }

    public Update set(RawColumnReference sqlColumn, Object value) {
        return set(sqlColumn, new JdbcParameter(value));
    }

    public Update setValueSetList(List<UpdateColumn> valueSetList) {
        for (UpdateColumn statement: valueSetList) {
            if (!dbTable.equals(statement.getColumn().getTable())) {
                throw new RuntimeException("X3");
            }
        }
        this.valueSetList = valueSetList;
        return this;
    }

    public Update join(Table right, BoolExpression boolExpression) {
        this.table = this.table.join(right, boolExpression, JoinType.inner);
        return this;
    }
    public Update leftJoin(Table right, BoolExpression boolExpression) {
        this.table = this.table.join(right, boolExpression, JoinType.left);
        return this;
    }

    public Update where(BoolExpression where) {
        this.where = where;
        return this;
    }

    public Update limit(Limit limit) {
        this.limit = limit;
        return this;
    }

    public Update limit(int value) {
        return limit(new Limit(value));
    }

    public Limit getLimit() {
        return limit;
    }

    public static class UpdateColumn {
        private RawColumnReference column;
        private Expression value;

        public UpdateColumn(RawColumnReference column, Expression value) {
            this.column = column;
            this.value = value;
        }

        public RawColumnReference getColumn() {
            return column;
        }

        public Expression getValue() {
            return value;
        }
    }

    public List<UpdateColumn> getValueSetList() {
        return valueSetList;
    }

    public Table getTable() {
        return table;
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public BoolExpression getWhere() {
        return where;
    }
}
