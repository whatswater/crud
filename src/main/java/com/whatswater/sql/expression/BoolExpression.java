package com.whatswater.sql.expression;


import com.whatswater.sql.expression.logic.AndExpression;
import com.whatswater.sql.expression.logic.OrExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface BoolExpression extends Expression {
    default AndExpression and(BoolExpression ...condition) {
        List<BoolExpression> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.addAll(Arrays.asList(condition));
        return new AndExpression(conditionList);
    }

    default OrExpression or(BoolExpression ...condition) {
        List<BoolExpression> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.addAll(Arrays.asList(condition));
        return new OrExpression(conditionList);
    }
}
