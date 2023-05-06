package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

public interface FieldGetter {
    Object get(Schema schema, Object idVal, Field field);
}
