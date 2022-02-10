package com.whatswater.sql.expression.literal;


import com.whatswater.sql.expression.Literal;

public class JdbcParameter implements Literal {
    private Object value;

    public JdbcParameter() {

    }
    public JdbcParameter(Object obj) {
        this.value = obj;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void visitAliasHolder(Handler handler) {

    }
}
