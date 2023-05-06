package com.whatswater.orm.dsl;

import com.whatswater.orm.dsl.criteria.And;

import java.util.Arrays;
import java.util.List;

public abstract class DSL {
    public static QueryCriteria and(QueryCriteria... condition) {
        List<QueryCriteria> conditionList = Arrays.asList(condition);
        return new And(conditionList);
    }

    public static QueryCriteria and(List<QueryCriteria> conditionList) {
        return new And(conditionList);
    }
}
