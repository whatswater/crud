package com.whatswater.sql.expression.bool;

import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class GreaterThan extends BinaryExpression implements BoolExpression {
    public GreaterThan(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return ">";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_GREATER_THAN;
    }

}
