package com.whatswater.orm.dsl;

import com.whatswater.orm.dsl.criteria.And;
import com.whatswater.orm.dsl.criteria.Or;
import com.whatswater.sql.expression.BoolExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface QueryCriteria {
    default And and(QueryCriteria... condition) {
        List<QueryCriteria> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.addAll(Arrays.asList(condition));
        return new And(conditionList);
    }

    default Or or(QueryCriteria ...condition) {
        List<QueryCriteria> conditionList = new ArrayList<>();
        conditionList.add(this);
        conditionList.addAll(Arrays.asList(condition));
        return new Or(conditionList);
    }

    QueryCriteria flatten();
}
