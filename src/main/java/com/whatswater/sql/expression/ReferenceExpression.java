package com.whatswater.sql.expression;


public interface ReferenceExpression extends Expression {
    @Override
    default ExpressionType type() {
        return ExpressionType.COLUMN_REF;
    }
}
