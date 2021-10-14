package com.whatswater.sql.expression.reference;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.AliasTable;

public class AliasColumnReference implements SelectColumn, Expression {
    private final AliasTable<?> table;
    private String columnName;
    private AliasPlaceholder columnAlias;

    public AliasColumnReference(AliasTable<?> table) {
        this.table = table;
    }

    public AliasColumnReference(AliasTable<?> table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    public AliasColumnReference(AliasTable<?> table, AliasPlaceholder columnAlias) {
        this.table = table;
        this.columnAlias = columnAlias;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.COLUMN_ALIAS_REF;
    }

    public AliasTable<?> getTable() {
        return table;
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return columnAlias;
    }

    @Override
    public String getAliasOrColumnName() {
        if (columnAlias != null && columnAlias.hasName()) {
            return columnAlias.getAlias();
        }
        return columnName;
    }

    public AliasColumnReference bindNewTable(AliasTable<?> table) {
        AliasColumnReference aliasColumnReference = new AliasColumnReference(table);
        aliasColumnReference.columnAlias = columnAlias;
        aliasColumnReference.columnName = columnName;

        return aliasColumnReference;
    }
}
