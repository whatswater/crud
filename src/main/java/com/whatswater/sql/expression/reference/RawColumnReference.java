package com.whatswater.sql.expression.reference;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.DbTable;

public class RawColumnReference implements SelectColumn, Expression {
    private final DbTable<?> table;
    private final String columnName;

    public RawColumnReference(DbTable<?> table, String columnName) {
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

    @Override
    public String getAliasOrColumnName() {
        return columnName;
    }

    public RawColumnReference bindNewTable(DbTable<?> newTable) {
        return new RawColumnReference(newTable, columnName);
    }
}
