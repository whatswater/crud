package com.whatswater.sql.expression.logic;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.LogicExpression;

import java.util.List;

public class OrExpression implements LogicExpression {
    List<BoolExpression> conditionList;

    public OrExpression() {

    }

    public OrExpression(List<BoolExpression> conditionList) {
        this.conditionList = conditionList;
    }

    public List<BoolExpression> getConditionList() {
        return conditionList;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        for (BoolExpression condition: conditionList) {
            condition.visitAliasHolder(handler);
        }
    }

    @Override
    public BoolExpression flatten() {
        if (conditionList == null) {
            return this;
        }

        if (conditionList.size() == 1) {
            return conditionList.get(0);
        }

        return this;
    }
}
