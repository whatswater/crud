package com.whatswater.orm.data;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;

public interface SchemaData {
    Schema getSchema();
    DataId getId();
    Object getData();
}
