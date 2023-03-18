package com.whatswater.orm.state;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.util.SchemaUtil;

import java.util.Map;

public class DataStore {
    private Map<String, Schema<?>> schemaMap;
    private Map<String, SchemaService<?>> schemaDataListServiceMap;
    private Map<String, SchemaListener> schemaListenerMap;

    public void installSchema(Schema<?> schema) {
        String schemaId = SchemaUtil.getSchemaId(schema);
        schemaMap.put(schemaId, schema);
        schemaDataListServiceMap.put(schemaId, createSchemaService(schema));
        schemaListenerMap.put(schemaId, new SchemaListener(schema));
    }

    public void dispatchAction(Schema<?> schema, Action<?> action) {
        String schemaId = SchemaUtil.getSchemaId(schema);
        SchemaListener schemaListener = schemaListenerMap.get(schemaId);
        schemaListener.onAction();
    }

    @SuppressWarnings("unchecked")
    public <T> SchemaService<T> getService(Schema<T> schema) {
        String schemaId = SchemaUtil.getSchemaId(schema);
        return (SchemaService<T>)schemaDataListServiceMap.get(schemaId);
    }

    private SchemaService<?> createSchemaService(Schema<?> schema) {
        return new SchemaService<>(schema, this);
    }
}
