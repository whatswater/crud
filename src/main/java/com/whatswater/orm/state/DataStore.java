package com.whatswater.orm.state;

import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.schema.SchemaManager;
import com.whatswater.orm.util.SchemaUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataStore {
    private SchemaManager schemaManager;
    private Map<String, SchemaService> schemaDataListServiceMap;

    public void installSchema(final Schema schema) {
        String schemaId = schemaManager.addOneSchema(schema);
        AtomicBoolean flag = new AtomicBoolean(false);
        SchemaService schemaService = schemaDataListServiceMap.computeIfAbsent(schemaId, key -> {
            flag.set(true);
            return createSchemaService(schema);
        });
        if (flag.get()) {
            return;
        }

        List<Schema> refs = schema.refSchemaList();
        for(Schema ref : refs) {
            installSchema(ref);
        }

        // listenList是refs的子集
        List<Schema> listenList = schema.listenSchemaList();
        for(Schema listen : listenList) {
            addListener(schemaService, listen);
        }
    }

    private void addListener(SchemaService consumeService, Schema productSchema) {
        SchemaService productSchemaService = getSchemaService(productSchema);
        productSchemaService.addListener(consumeService);
    }

    public SchemaService getSchemaService(Schema schema) {
        String schemaId = SchemaUtil.getSchemaId(schema);
        return schemaDataListServiceMap.get(schemaId);
    }

    private SchemaService createSchemaService(Schema schema) {
        return new SchemaService(schema, this);
    }
}
