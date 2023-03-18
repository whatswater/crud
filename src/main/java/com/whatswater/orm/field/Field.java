package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

public interface Field {
    Schema<?> getSchema();
    String getTypeDescription();
    String getPropertyName();
}
