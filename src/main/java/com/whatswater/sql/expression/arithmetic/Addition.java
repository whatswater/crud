package com.whatswater.sql.expression.arithmetic;

import com.whatswater.sql.expression.ArithmeticExpression;
import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;

public class Addition extends BinaryExpression implements ArithmeticExpression {
    public Addition(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "+";
    }
}
