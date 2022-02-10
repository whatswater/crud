package com.whatswater.sql.expression.reference;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.utils.StringUtils;

public class AliasColumnReference implements SelectColumn, ReferenceExpression {
    private final AliasTable<?> table;
    private final AliasPlaceholder columnAlias;

    public AliasColumnReference(AliasTable<?> table, AliasPlaceholder columnAlias) {
        this.table = table;
        this.columnAlias = columnAlias;
    }

    public AliasTable<?> getTable() {
        return table;
    }

    @Override
    public String getAliasOrColumnName() {
        if (columnAlias.hasName()) {
            return columnAlias.getAlias();
        }
        return null;
    }

    @Override
    public boolean matchColumnName(String columnName) {
        if (columnAlias.hasName()) {
            return columnAlias.getAlias().equals(columnName);
        }
        return false;
    }

    @Override
    public boolean matchColumnName(AliasPlaceholder aliasPlaceholder) {
        if (columnAlias == aliasPlaceholder) {
            return true;
        }

        String alias1 = columnAlias.getAlias();
        String alias2 = aliasPlaceholder.getAlias();
        if (StringUtils.isEmpty(alias1) || StringUtils.isEmpty(alias2)) {
            return false;
        }
        return alias1.equals(alias2);
    }

    public AliasColumnReference bindNewTable(AliasTable<?> table) {
        return new AliasColumnReference(table, columnAlias);
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        handler.handle(columnAlias);
    }

    public AliasPlaceholder getColumnAlias() {
        return columnAlias;
    }
}
