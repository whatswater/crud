package com.whatswater.sql.expression.logic;

import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.LogicExpression;

public class NotExpression implements LogicExpression {
    private Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        expression.visitAliasHolder(handler);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public BoolExpression flatten() {
        return this;
    }
}
