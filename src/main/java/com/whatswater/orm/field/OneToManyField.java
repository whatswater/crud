package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

public class OneToManyField implements Field {
    private Schema schema;
    private String name;
    private Schema refSchema;

    @Override
    public FieldType type() {
        return FieldType.ONE_TO_MANY;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public String getTypeDescription() {
        return "Array<" + refSchema.schemaName() + ">";
    }

    @Override
    public String getPropertyName() {
        return name;
    }

    public Schema getRefSchema() {
        return refSchema;
    }
}
