package com.whatswater.orm.dsl.criteria;

import com.whatswater.orm.dsl.QueryCriteria;

public class Not implements QueryCriteria {
    private QueryCriteria queryCriteria;

    public Not(QueryCriteria queryCriteria) {
        this.queryCriteria = queryCriteria;
    }

    @Override
    public QueryCriteria flatten() {
        return this;
    }
}
