package com.whatswater.orm.data;

import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.schema.Schema;

import java.util.List;

public interface SchemaDataList {
    Schema getSchema();
    List<DataId> getIdList();
    List<Object> getDataList();
    SchemaData get(int index);
}
