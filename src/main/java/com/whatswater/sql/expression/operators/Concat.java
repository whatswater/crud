package com.whatswater.sql.expression.operators;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;

public class Concat extends BinaryExpression {
    public Concat(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "||";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.OP_CONCAT;
    }
}
