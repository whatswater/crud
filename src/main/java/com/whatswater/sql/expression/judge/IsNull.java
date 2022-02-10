package com.whatswater.sql.expression.judge;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.JudgeOperatorExpression;

public class IsNull implements JudgeOperatorExpression {
    private final Expression expression;

    public IsNull(Expression expression) {
        this.expression = expression;
    }

    public Expression getLeft() {
        return expression;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        expression.visitAliasHolder(handler);
    }
}
