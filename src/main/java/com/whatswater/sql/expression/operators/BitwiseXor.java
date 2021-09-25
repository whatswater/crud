package com.whatswater.sql.expression.operators;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;


public class BitwiseXor extends BinaryExpression {
    public BitwiseXor(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "^";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.OP_BIT_XOR;
    }
}
