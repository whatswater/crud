package com.whatswater.sql.expression.reference;


import com.whatswater.sql.alias.AliasPlaceholderGetter;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.TableCanRef;
import com.whatswater.sql.utils.StringUtils;

public class AliasColumnRef implements SelectColumn, Expression, AliasPlaceholderGetter {
    private TableCanRef table;
    private String columnName;
    private AliasPlaceholder columnAlias;

    public AliasColumnRef(TableCanRef table, String columnName) {
        this.table = table;
        this.columnName = columnName;
    }

    public AliasColumnRef(TableCanRef table, AliasPlaceholder columnAlias) {
        this.table = table;
        this.columnAlias = columnAlias;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.COLUMN_ALIAS_REF;
    }

    public TableCanRef getTable() {
        return table;
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return columnAlias;
    }

    @Override
    public boolean hasAlias() {
        if (StringUtils.isNotEmpty(columnName)) {
            return true;
        }

        return columnAlias != null && columnAlias.hasName();
    }

    public String getAliasOrColumnName() {
        if (hasAlias()) {
            return columnAlias.getAlias();
        }
        return columnName;
    }
}
