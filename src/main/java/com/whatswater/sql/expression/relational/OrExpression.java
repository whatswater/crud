package com.whatswater.sql.expression.relational;


import com.whatswater.sql.expression.BoolExpression;

import java.util.List;

public class OrExpression implements BoolExpression {
    List<BoolExpression> conditionList;

    public OrExpression() {

    }

    public OrExpression(List<BoolExpression> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_OR;
    }

    public List<BoolExpression> getConditionList() {
        return conditionList;
    }
}
