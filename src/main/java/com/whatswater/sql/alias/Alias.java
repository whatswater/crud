package com.whatswater.sql.alias;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.utils.StringUtils;

public class Alias implements SelectColumn, AliasPlaceholderGetter {
    private Expression expression;
    private AliasPlaceholder aliasPlaceholder;
    private String aliasName;

    public Alias(Expression expression, AliasPlaceholder placeholder) {
        this.expression = expression;
        this.aliasPlaceholder = placeholder;
    }

    public Alias(Expression expression, String aliasName) {
        this.expression = expression;
        this.aliasName = aliasName;
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public boolean hasAlias() {
        if (StringUtils.isNotEmpty(aliasName)) {
            return true;
        }

        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }
}
