package com.whatswater.orm.schema;


import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.FieldType;
import com.whatswater.orm.field.ForeignKeyField;
import com.whatswater.orm.field.OneToManyField;
import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.field.list.BasicFieldList;

import java.util.ArrayList;
import java.util.List;


public class BasicSchema implements Schema {
    private final String packageName;
    private final String schemaName;
    private final BasicFieldList properties;

    public BasicSchema(String packageName, String schemaName, BasicFieldList properties) {
        this.packageName = packageName;
        this.schemaName = schemaName;
        this.properties = properties;
    }

    @Override
    public String moduleName() {
        return packageName;
    }

    @Override
    public String schemaName() {
        return schemaName;
    }

    @Override
    public List<Schema> refSchemaList() {
        List<Schema> refs = new ArrayList<>();
        // 计算属性只能引用当前Schema中的属性，所以不需要计算ref
        for(Field property : properties.properties()) {
            FieldType fieldType = property.type();
            if (fieldType == FieldType.ONE_TO_MANY) {
                addRefs(refs, (OneToManyField) property);
            } else if (fieldType == FieldType.FOREIGN_KEY) {
                addRefs(refs, (ForeignKeyField) property);
            }
        }
        return refs;
    }

    @Override
    public List<Schema> listenSchemaList() {
        return refSchemaList();
    }

    @Override
    public DataId getPrimaryKeyValue(Object data) {
        return null;
    }

    @Override
    public BasicFieldList fieldList() {
        return properties;
    }

    private static void addRefs(List<Schema> refs, ForeignKeyField field) {

    }
    private static void addRefs(List<Schema> refs, OneToManyField field) {

    }
}
