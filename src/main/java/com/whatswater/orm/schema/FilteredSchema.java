package com.whatswater.orm.schema;

import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.list.BasicFieldList;
import com.whatswater.orm.field.list.FieldList;
import com.whatswater.orm.schema.index.Index;
import com.whatswater.orm.util.MetaKey;

public class FilteredSchema<T> implements Schema<T> {
    private final String moduleName;
    private final String schemaName;
    private FieldList fieldList;
    private Schema<T> baseSchema;

    public FilteredSchema(String name, Schema<T> baseSchema) {
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
    public FieldList fieldList() {
        return fieldList;
    }

    @Override
    public Field findField(String name) {
        return null;
    }

    @Override
    public Field findField(String name, String type) {
        return null;
    }

    @Override
    public <S extends Index> S getIndex(MetaKey<S> metaKey) {
        return null;
    }
}
