package com.whatswater.orm.dsl.criteria;

import com.whatswater.orm.dsl.QueryCriteria;

public class QueryParam implements QueryCriteria {
    private String paramName;
    private Object paramValue;

    public QueryParam(String paramName, Object paramValue) {
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Object getParamValue() {
        return paramValue;
    }

    public void setParamValue(Object paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public QueryCriteria flatten() {
        return this;
    }
}
