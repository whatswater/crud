package com.whatswater.orm.dsl.criteria;

import com.whatswater.orm.dsl.QueryCriteria;

import java.util.List;

public class Or implements QueryCriteria {
    List<QueryCriteria> conditionList;

    public Or() {

    }

    public Or(List<QueryCriteria> conditionList) {
        this.conditionList = conditionList;
    }

    public List<QueryCriteria> getConditionList() {
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
