package com.whatswater.sql.alias;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.utils.StringUtils;

public class Alias implements SelectColumn {
    private Expression expression;
    private AliasPlaceholder aliasPlaceholder;

    public Alias(Expression expression, AliasPlaceholder placeholder) {
        this.expression = expression;
        this.aliasPlaceholder = placeholder;
    }

    public Alias(Expression expression, String aliasName) {
        this.expression = expression;
        this.aliasPlaceholder = new AliasPlaceholder(aliasName);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public String getAliasOrColumnName() {
        String alias = aliasPlaceholder.getAlias();
        if (StringUtils.isEmpty(alias)) {
            throw new RuntimeException("X3");
        }
        return alias;
    }

    public Expression getExpression() {
        return expression;
    }
}
