package com.whatswater.sql.expression;


public interface LogicExpression extends BoolExpression {
    @Override
    default ExpressionType type() {
        return ExpressionType.LOGIC_OPERATOR;
    }
    BoolExpression flatten();
}
