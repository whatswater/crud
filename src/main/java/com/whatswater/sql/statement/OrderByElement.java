package com.whatswater.sql.statement;

import com.whatswater.sql.expression.Expression;

public class OrderByElement {
    private Expression expression;
    private boolean asc = true;
    private NullOrdering nullOrdering = NullOrdering.NULLS_FIRST;

    public OrderByElement(Expression expression, boolean asc) {
        this.expression = expression;
        this.asc = asc;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isAsc() {
        return asc;
    }

    public NullOrdering getNullOrdering() {
        return nullOrdering;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(this.expression.toString());
        if (!this.asc) {
            b.append(" DESC");
        }
        if (this.nullOrdering != null) {
            b.append(' ');
            b.append(this.nullOrdering == NullOrdering.NULLS_FIRST ? "NULLS FIRST" : "NULLS LAST");
        }
        return b.toString();
    }

    public enum NullOrdering {
        NULLS_FIRST,
        NULLS_LAST;

        private NullOrdering() {
        }
    }
}
