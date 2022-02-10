package com.whatswater.sql.alias;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.utils.StringUtils;

public class Alias implements SelectColumn {
    private final Expression expression;
    private final AliasPlaceholder aliasPlaceholder;

    public Alias(Expression expression, AliasPlaceholder placeholder) {
        this.expression = expression;
        this.aliasPlaceholder = placeholder;
    }

    public Alias(Expression expression, String aliasName) {
        this.expression = expression;
        this.aliasPlaceholder = new AliasPlaceholder(aliasName);
    }

    @Override
    public String getAliasOrColumnName() {
        String alias = aliasPlaceholder.getAlias();
        if (StringUtils.isEmpty(alias)) {
            throw new RuntimeException("X3");
        }
        return alias;
    }

    @Override
    public boolean matchColumnName(String columnName) {
        String alias = aliasPlaceholder.getAlias();
        if (StringUtils.isEmpty(alias)) {
            return false;
        }
        return alias.equals(columnName);
    }

    @Override
    public boolean matchColumnName(AliasPlaceholder columnName) {
        if (aliasPlaceholder == columnName) {
            return true;
        }

        String alias1 = aliasPlaceholder.getAlias();
        String alias2 = columnName.getAlias();
        if (StringUtils.isEmpty(alias1) || StringUtils.isEmpty(alias2)) {
            return false;
        }

        return alias1.equals(alias2);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        handler.handle(aliasPlaceholder);
    }

    public AliasPlaceholder getAliasPlaceholder() {
        return aliasPlaceholder;
    }
}
