package com.whatswater.sql.expression.reference;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.AliasTable;

public class RawColumnReference implements SelectColumn, ReferenceExpression {
    private final AliasTable<?> table;
    private final String columnName;

    public RawColumnReference(AliasTable<?> table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.COLUMN_REF;
    }

    public AliasTable<?> getTable() {
        return table;
    }

    @Override
    public String getAliasOrColumnName() {
        return columnName;
    }

    @Override
    public boolean matchColumnName(String columnName) {
        return columnName.equals(columnName);
    }

    @Override
    public boolean matchColumnName(AliasPlaceholder columnName) {
        return false;
    }

    @Override
    public void visitAliasHolder(Handler handler) {

    }

    public String getColumnName() {
        return columnName;
    }

    public RawColumnReference bindNewTable(AliasTable<?> newTable) {
        return new RawColumnReference(newTable, columnName);
    }
}
