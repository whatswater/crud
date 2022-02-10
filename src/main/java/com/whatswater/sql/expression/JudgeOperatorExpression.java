package com.whatswater.sql.expression;

public interface JudgeOperatorExpression extends BoolExpression {
    @Override
    default ExpressionType type() {
        return ExpressionType.JUDGE_OPERATOR;
    }
}
