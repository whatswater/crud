package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;

public class Between implements BoolExpression {
    private Expression leftValue;
    private boolean not = false;
    private Expression start;
    private Expression end;

    @Override
    public String toString() {
        return this.leftValue + " " + (this.not ? "NOT " : "") + "BETWEEN " + this.start + " AND " + this.end;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_BETWEEN_AND;
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
}
