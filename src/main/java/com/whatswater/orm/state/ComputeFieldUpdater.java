package com.whatswater.orm.state;

import com.whatswater.orm.field.ComputeField;
import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.FieldGetter;
import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;

import java.util.HashMap;
import java.util.Map;

// 标记schema -> dataId -> property 是否是 dirty，当前的值是什么
public class ComputeFieldUpdater implements FieldGetter {
    private DataStore dataStore;

    @Override
    public Object get(Schema schema, Object idVal, Field field) {
        return null;
    }

    // 惰性求值的缓存
    Map<FieldValue, FieldValue> values = new HashMap<>();

    public void markDirty(Schema schema, ComputeField field, DataId id) {
        FieldValue key = new FieldValue(schema, field.getPropertyName(), id);
        FieldValue value = values.putIfAbsent(key, key);
        if (value == null) {
            value = key;
        }
        value.setDirty(true);
    }

    // 主要是内存计算还是什么计算？
    // 开启事务内缓存？
    // 计算出错应该如何做？
    // 如何校验非法数据？
    public void startCompute() {

    }
}
