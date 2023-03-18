package com.whatswater.orm.schema;

import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.list.FieldList;
import com.whatswater.orm.field.list.PickedFieldList;
import com.whatswater.orm.schema.index.Index;
import com.whatswater.orm.util.MetaKey;

import java.util.Set;

public class PickedSchema<R, T> implements Schema<R> {
    private final String moduleName;
    private final String schemaName;
    private FieldList fieldList;
    private Schema<T> baseSchema;

    public PickedSchema(
        String name,
        Schema<T> baseSchema,
        Set<String> pickPropertyNames
    ) {
        this.baseSchema = baseSchema;
        this.moduleName = baseSchema.moduleName();
        this.schemaName = name;
        this.fieldList = new PickedFieldList(baseSchema.fieldList(), pickPropertyNames);
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
        return fieldList.findProperty(name);
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
