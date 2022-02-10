package com.whatswater.sql.expression.judge;


import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.JudgeOperatorExpression;

public class Between implements JudgeOperatorExpression {
    private Expression leftValue;
    private boolean not = false;
    private Expression start;
    private Expression end;

    @Override
    public String toString() {
        return this.leftValue + " " + (this.not ? "NOT " : "") + "BETWEEN " + this.start + " AND " + this.end;
    }

    public Expression getLeftValue() {
        return leftValue;
    }

    public boolean isNot() {
        return not;
    }

    public Expression getStart() {
        return start;
    }

    public Expression getEnd() {
        return end;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        leftValue.visitAliasHolder(handler);
        start.visitAliasHolder(handler);
        end.visitAliasHolder(handler);
    }
}
