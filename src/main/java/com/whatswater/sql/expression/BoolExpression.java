package com.whatswater.sql.expression;


import com.whatswater.sql.expression.relational.AndExpression;
import com.whatswater.sql.expression.relational.OrExpression;

import java.util.ArrayList;
import java.util.List;

public interface BoolExpression extends Expression {
    default AndExpression and(BoolExpression condition) {
        List<BoolExpression> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.add(condition);
        return new AndExpression(conditionList);
    }

    default OrExpression or(BoolExpression condition) {
        List<BoolExpression> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.add(condition);
        return new OrExpression(conditionList);
    }

}
