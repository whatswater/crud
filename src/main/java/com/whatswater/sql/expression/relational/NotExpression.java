package com.whatswater.sql.expression.relational;

import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class NotExpression implements BoolExpression {
    private Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_NOT;
    }
}
