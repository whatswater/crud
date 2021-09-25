package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class LessThan extends BinaryExpression implements BoolExpression {
    public LessThan(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "<";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_LESS_THAN;
    }
}
