package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

import java.util.Collections;
import java.util.List;

public interface Field {
    List<Schema> EMPTY_REFS = Collections.emptyList();

    FieldType type();
    Schema getSchema();
    String getTypeDescription();
    String getPropertyName();
}
