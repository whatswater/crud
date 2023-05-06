package com.whatswater.orm.schema;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.field.list.BasicFieldList;
import com.whatswater.orm.field.list.FieldList;

import java.util.List;

public class FilteredSchema implements Schema {
    private final String moduleName;
    private final String schemaName;
    private FieldList fieldList;
    private Schema baseSchema;

    public FilteredSchema(String name, Schema baseSchema) {
        this.moduleName = baseSchema.moduleName();
        this.schemaName = name;
        this.fieldList = new BasicFieldList(baseSchema.fieldList());
        this.baseSchema = baseSchema;
    }

    @Override
    public String moduleName() {
        return moduleName;
    }

    @Override
    public String schemaName() {
        return schemaName;
    }

    @Override
    public List<Schema> refSchemaList() {
        return baseSchema.refSchemaList();
    }

    @Override
    public List<Schema> listenSchemaList() {
        return baseSchema.listenSchemaList();
    }

    @Override
    public DataId getPrimaryKeyValue(Object data) {
        return null;
    }

    @Override
    public FieldList fieldList() {
        return fieldList;
    }
}
