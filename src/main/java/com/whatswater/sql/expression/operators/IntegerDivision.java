package com.whatswater.sql.expression.operators;

import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;

public class IntegerDivision extends BinaryExpression {
    public IntegerDivision(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "INT_DIV";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.OP_INTEGER_DIV;
    }
}
