package com.whatswater.sql.expression.relational;


import com.whatswater.sql.expression.BoolExpression;

import java.util.List;

public class AndExpression implements BoolExpression {
    List<BoolExpression> conditionList;

    public AndExpression() {
    }

    public AndExpression(List<BoolExpression> conditionList) {
        this.conditionList = conditionList;
    }

    @Override
    public ExpressionType type() {
        return ExpressionType.RELATION_AND;
    }

    public List<BoolExpression> getConditionList() {
        return conditionList;
    }
}
