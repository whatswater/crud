package com.whatswater.sql.expression.reference;


import com.whatswater.sql.expression.Expression;

public class JdbcParameter implements Expression {
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
    public ExpressionType type() {
        return ExpressionType.JDBC_PARAMETER;
    }
}
