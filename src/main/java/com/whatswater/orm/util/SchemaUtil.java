package com.whatswater.orm.util;

import com.whatswater.orm.schema.Schema;

public abstract class SchemaUtil {
    public static boolean isNotNullOrEmpty(String s) {
        return s != null || s.length() > 0;
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String getSchemaId(Schema<?> schema) {
        return schema.moduleName() + "/" + schema.schemaName();
    }

    public static String getSchemaTypeDescription(Schema<?> schema) {
        return "schema " + getSchemaId(schema);
    }
}
