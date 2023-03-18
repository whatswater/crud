package com.whatswater.orm.schema;


import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.list.BasicFieldList;
import com.whatswater.orm.schema.index.Index;
import com.whatswater.orm.util.MetaKey;


public class BasicSchema<T> implements Schema<T> {
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
    public BasicFieldList fieldList() {
        return properties;
    }

    @Override
    public Field findField(String name) {
        return properties.findProperty(name);
    }

    @Override
    public Field findField(String name, String type) {
        Field ret = properties.findProperty(name);
        if (type.equals(ret.getTypeDescription())) {
            return ret;
        }
        return null;
    }

    @Override
    public <S extends Index> S getIndex(MetaKey<S> metaKey) {
        return null;
    }
}
