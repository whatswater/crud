package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class IsNull implements BoolExpression {
    private Expression expression;

    public IsNull(Expression expression) {
        this.expression = expression;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_IS_NULL;
    }

    public Expression getLeft() {
        return expression;
    }
}
