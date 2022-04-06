package com.whatswater.nothing.data;

import com.whatswater.nothing.property.Property;
import com.whatswater.nothing.schema.DbSchema;

public class ModelData {
    private DbSchema schema;
    private Object[] data;

    public ModelData(DbSchema schema, Object[] data) {
        this.schema = schema;
        this.data = data;
    }

    // 根据property获取到index，然后取出
    public <V> V getProperty(Property<V> property) {
        int idx = 0;
        return (V)data[idx];
    }
}
