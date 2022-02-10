package com.whatswater.sql.expression;


public interface ArithmeticExpression extends Expression {
    @Override
    default ExpressionType type() {
        return ExpressionType.ARITHMETIC_OPERATOR;
    }
}
