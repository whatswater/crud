package com.whatswater.orm.field;


import com.whatswater.orm.schema.Schema;

/**
 * Schema的基础属性
 */
public class BasicField implements Field {
    private final Schema schema;
    private final String type;
    private final String propertyName;

    public BasicField(Schema schema, String type, String propertyName) {
        this.schema = schema;
        this.type = type;
        this.propertyName = propertyName;
    }

    @Override
    public FieldType type() {
        return FieldType.BASIC;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public String getTypeDescription() {
        return this.type;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }
}
