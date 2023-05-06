package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.util.SchemaUtil;

// 外键引用
public class ForeignKeyField implements Field {
    private Schema schema;
    private String propertyName;
    private Schema fkSchema;
    private String fkPropertyName;

    @Override
    public FieldType type() {
        return FieldType.FOREIGN_KEY;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public String getTypeDescription() {
        return SchemaUtil.getSchemaTypeDescription(fkSchema);
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    public Schema getFkSchema() {
        return fkSchema;
    }

    public String getFkPropertyName() {
        return fkPropertyName;
    }
}
