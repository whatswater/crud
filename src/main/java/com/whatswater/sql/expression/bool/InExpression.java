package com.whatswater.sql.expression.bool;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.literal.Literal;

import java.util.Arrays;
import java.util.List;

public class InExpression implements BoolExpression {
    private Expression leftValue;
    private ItemList inList;

    public InExpression(Expression leftValue, ItemList inList) {
        this.leftValue = leftValue;
        this.inList = inList;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_IN;
    }

    public interface ItemList {
    }

    public static class ExpressionList implements ItemList {
        List<Expression> expressions;

        public ExpressionList() {
        }

        public ExpressionList(List<Expression> expressions) {
            this.expressions = expressions;
        }

        public ExpressionList(Expression... expressions) {
            this.expressions = Arrays.asList(expressions);
        }

        public List<Expression> getExpressions() {
            return this.expressions;
        }

        public void setExpressions(List<Expression> list) {
            this.expressions = list;
        }
    }

    public static class LiteralList implements ItemList {
        List<Literal> literals;

        public LiteralList() {

        }

        public LiteralList(List<Literal> literals) {
            this.literals = literals;
        }

        public List<Literal> getLiterals() {
            return literals;
        }

        public void setLiterals(List<Literal> literals) {
            this.literals = literals;
        }
    }

    public static class SubSelect implements ItemList {
        // ...
    }
}
