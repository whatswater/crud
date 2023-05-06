package com.whatswater.orm.dsl.criteria;

import com.whatswater.orm.dsl.QueryCriteria;

import java.util.List;

public class And implements QueryCriteria {
    List<QueryCriteria> conditionList;

    public And() {
    }

    public And(List<QueryCriteria> conditionList) {
        this.conditionList = conditionList;
    }

    public List<? extends QueryCriteria> getConditionList() {
        return conditionList;
    }

    @Override
    public QueryCriteria flatten() {
        if (conditionList == null) {
            return this;
        }
        if (conditionList.size() == 1) {
            return conditionList.get(0);
        }
        return this;
    }
}
