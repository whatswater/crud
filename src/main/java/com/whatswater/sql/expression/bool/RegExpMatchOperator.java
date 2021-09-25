package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BinaryExpression;
import com.whatswater.sql.expression.Expression;

public class RegExpMatchOperator extends BinaryExpression {
    private RegExpMatchOperatorType operatorType;

    public RegExpMatchOperator(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    public String getOperatorString() {
        switch(this.operatorType) {
            case MATCH_CASESENSITIVE:
                return "~";
            case MATCH_CASEINSENSITIVE:
                return "~*";
            case NOT_MATCH_CASESENSITIVE:
                return "!~";
            case NOT_MATCH_CASEINSENSITIVE:
                return "!~*";
            default:
                return null;
        }
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_REGEX_MATCH;
    }

    public enum RegExpMatchOperatorType {
        MATCH_CASESENSITIVE,
        MATCH_CASEINSENSITIVE,
        NOT_MATCH_CASESENSITIVE,
        NOT_MATCH_CASEINSENSITIVE;
    }
}
