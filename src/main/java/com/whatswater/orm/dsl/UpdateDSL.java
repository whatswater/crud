package com.whatswater.orm.dsl;

import com.whatswater.orm.field.Field;
import com.whatswater.orm.schema.Schema;

import java.util.List;

public class UpdateDSL {
    Schema schema;
    List<Field> updateFieldList;
    List<Object> valueList;    // value可能是SQL表达式
    QueryCriteria queryParam;

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public List<Field> getUpdateFieldList() {
        return updateFieldList;
    }

    public void setUpdateFieldList(List<Field> updateFieldList) {
        this.updateFieldList = updateFieldList;
    }

    public List<Object> getValueList() {
        return valueList;
    }

    public void setValueList(List<Object> valueList) {
        this.valueList = valueList;
    }

    public QueryCriteria getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryCriteria queryParam) {
        this.queryParam = queryParam;
    }
}
