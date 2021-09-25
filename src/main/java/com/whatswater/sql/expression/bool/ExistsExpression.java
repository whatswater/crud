package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.Expression;

public class ExistsExpression implements Expression {
    private Expression right;
    private boolean not = false;

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_EQUAL;
    }

    public boolean isNot() {
        return this.not;
    }

    public Expression getRight() {
        return right;
    }

    public String getStringExpression() {
        return (this.not ? "NOT " : "") + "EXISTS";
    }

    @Override
    public String toString() {
        return this.getStringExpression() + " " + this.right.toString();
    }
}
