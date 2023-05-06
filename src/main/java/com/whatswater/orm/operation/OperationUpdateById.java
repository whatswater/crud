package com.whatswater.orm.operation;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.dsl.QueryCriteria;
import com.whatswater.orm.dsl.UpdateDSL;
import com.whatswater.orm.field.Field;
import com.whatswater.orm.schema.Schema;

import java.util.List;


public class OperationUpdateById implements Operation {
    final UpdateDSL dsl;
    final DataId dataId;

    public OperationUpdateById(Schema schema, DataId dataId, List<Field> fieldList, List<Object> valueList) {
        QueryCriteria queryParam = dataId.toQueryCriteria();
        UpdateDSL dsl = new UpdateDSL();
        dsl.setSchema(schema);
        dsl.setQueryParam(queryParam);
        dsl.setValueList(valueList);
        dsl.setUpdateFieldList(fieldList);
        this.dataId = dataId;
        this.dsl = dsl;
    }

    public UpdateDSL getDsl() {
        return dsl;
    }

    public DataId getDataId() {
        return dataId;
    }

    @Override
    public OperationType operationType() {
        return OperationType.UPDATE_BY_ID;
    }
}
