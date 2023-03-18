package com.whatswater.orm.state;

import com.whatswater.orm.schema.Schema;
import com.whatswater.orm.storage.StorageService;

public class SchemaService<T> {
    private DataStore dataStore;
    private Schema<T> schema;

    // 存储
    private StorageService storageService;
    // Action

    public SchemaService(Schema<T> schema, DataStore dataStore) {
        this.schema = schema;
        this.dataStore = dataStore;
    }
}
