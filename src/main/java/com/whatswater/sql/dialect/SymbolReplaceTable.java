package com.whatswater.sql.dialect;

import com.whatswater.sql.expression.ReferenceExpression;


public class SymbolReplaceTable {
    ReferenceExpression oldExpression;
    ReferenceExpression newExpression;

    public ReferenceExpression getOldExpression() {
        return oldExpression;
    }

    public void setOldExpression(ReferenceExpression oldExpression) {
        this.oldExpression = oldExpression;
    }

    public ReferenceExpression getNewExpression() {
        return newExpression;
    }

    public void setNewExpression(ReferenceExpression newExpression) {
        this.newExpression = newExpression;
    }
}
