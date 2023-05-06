package com.whatswater.orm.schema;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.field.list.FieldList;
import com.whatswater.orm.field.list.PickedFieldList;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PickedSchema implements Schema {
    private final String moduleName;
    private final String schemaName;
    private final FieldList fieldList;
    private final Schema baseSchema;

    public PickedSchema(
        String name,
        Schema baseSchema,
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
    public List<Schema> refSchemaList() {
        return Collections.singletonList(baseSchema);
    }

    @Override
    public List<Schema> listenSchemaList() {
        return Collections.emptyList();
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
