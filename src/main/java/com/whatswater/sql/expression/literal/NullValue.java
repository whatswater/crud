package com.whatswater.sql.expression.literal;

import com.whatswater.sql.expression.Expression;

public class NullValue implements Literal {
    @Override
    public String toString() {
        return "NULL";
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.VALUE_NULL;
    }
}
