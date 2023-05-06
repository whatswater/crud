package com.whatswater.orm.data.id;

import com.whatswater.orm.dsl.QueryCriteria;

public interface DataId {
    Object getIdValue();
    QueryCriteria toQueryCriteria();
}
