package com.whatswater.sql.expression.literal;

import com.whatswater.sql.expression.Literal;

public class NullValue implements Literal {
    @Override
    public String toString() {
        return "NULL";
    }
}
