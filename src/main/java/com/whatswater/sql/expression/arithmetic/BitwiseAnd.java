package com.whatswater.sql.expression.arithmetic;

import com.whatswater.sql.expression.ArithmeticExpression;
import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;

public class BitwiseAnd extends BinaryExpression implements ArithmeticExpression {
    public BitwiseAnd(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "&";
    }
}
