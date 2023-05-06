package com.whatswater.orm.data.id;

import com.whatswater.orm.dsl.DSL;
import com.whatswater.orm.dsl.QueryCriteria;
import com.whatswater.orm.dsl.criteria.QueryParam;
import com.whatswater.orm.field.Field;
import com.whatswater.orm.schema.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 主属性列表
 */
public class MainProperties implements DataId {
    private List<Field> mainFieldList;
    private List<Object> valueList;

    @Override
    public List<Object> getIdValue() {
        return valueList;
    }

    @Override
    public QueryCriteria toQueryCriteria() {
        List<QueryCriteria> list = new ArrayList<>(mainFieldList.size());
        for(int i = 0; i < mainFieldList.size(); i++) {
            QueryParam queryParam = new QueryParam(mainFieldList.get(i).getPropertyName(), valueList.get(i));
            list.add(queryParam);
        }
        return DSL.and(list);
    }

    public List<Field> getMainFieldList() {
        return mainFieldList;
    }
}
