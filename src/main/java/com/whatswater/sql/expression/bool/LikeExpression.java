package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class LikeExpression extends BinaryExpression implements BoolExpression {
    public LikeExpression(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "LIKE";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_LIKE;
    }
}
