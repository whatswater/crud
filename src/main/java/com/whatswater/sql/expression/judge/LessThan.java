package com.whatswater.sql.expression.judge;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.JudgeOperatorExpression;

public class LessThan extends BinaryExpression implements JudgeOperatorExpression {
    public LessThan(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        return "<";
    }
}
