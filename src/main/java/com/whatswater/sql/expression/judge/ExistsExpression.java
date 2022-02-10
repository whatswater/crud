package com.whatswater.sql.expression.judge;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.JudgeOperatorExpression;

public class ExistsExpression implements JudgeOperatorExpression {
    private Expression right;
    private boolean not = false;

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

    @Override
    public void visitAliasHolder(Handler handler) {
        right.visitAliasHolder(handler);
    }
}
