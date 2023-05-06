package com.whatswater.orm.data.id;

import com.whatswater.orm.dsl.QueryCriteria;
import com.whatswater.orm.dsl.criteria.QueryParam;
import com.whatswater.orm.schema.Schema;

public abstract class SingleValueId<S> implements DataId {
    private Schema schema;
    private String propName;
    private S value;

    @Override
    public S getIdValue() {
        return value;
    }

    @Override
    public QueryCriteria toQueryCriteria() {
        return new QueryParam(propName, value);
    }
}
