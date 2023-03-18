package com.whatswater.orm.state;

import com.whatswater.orm.field.ForeignKeyField;
import com.whatswater.orm.field.Field;
import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.util.SchemaUtil;

import java.util.Map;

// 监听器不能成环，若成环之后会照成死锁
// 如何标记数据是dirty的
public class SchemaListener {
    private final Schema<?> schema;
    // 监听器
    private Map<String, Object> listenerMap;

    public SchemaListener(Schema<?> schema) {
        this.schema = schema;
    }

//    private void init() {
//        for(Field<?> property : schema.properties().properties()) {
//            if (property instanceof ForeignKeyField) {
//                ForeignKeyField<?, ?> foreignKeyField = (ForeignKeyField<?, ?>) property;
//                if (SchemaUtil.isNotNullOrEmpty(foreignKeyField.getFkPropertyName())) {
//
//                }
//            }
//        }
//    }

    public void onAction() {

    }

    public interface Watcher {

    }
}
