package com.whatswater.sql.expression;


public interface Literal extends Expression {
    @Override
    default ExpressionType type() {
        return ExpressionType.LITERAL;
    }

    @Override
    default void visitAliasHolder(Handler handler) {

    }
}
