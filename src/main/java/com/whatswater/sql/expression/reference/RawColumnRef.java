package com.whatswater.sql.expression.reference;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.DbTable;

public class RawColumnRef implements SelectColumn, Expression {
    private DbTable<?> table;
    private String columnName;

    public RawColumnRef(DbTable<?> table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.COLUMN_REF;
    }

    public DbTable<?> getTable() {
        return table;
    }

    public String getColumnName() {
        return columnName;
    }
}
