package com.whatswater.orm.schema;

import com.whatswater.orm.state.SchemaService;
import com.whatswater.orm.util.SchemaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

// 多个Schema的管理者
public class SchemaManager {
    private static class SchemaServiceHolder {
        private Schema schema;
        private SchemaService schemaService;
    }

    private NavigableMap<String, Schema> schemaMap;

    public String addOneSchema(Schema schema) {
        List<Schema> refs = schema.refSchemaList();
        if (refs != null) {
            for(int i = 0; i < refs.size(); i++) {
                addOneSchema(schema);
            }
        }
        String schemaId = SchemaUtil.getSchemaId(schema);
        schemaMap.putIfAbsent(schemaId, schema);
        return schemaId;
    }

    public Schema getOneByName(String name) {
        return schemaMap.get(name);
    }

    public List<Schema> listByModuleName(String packageName) {
        NavigableMap<String, Schema> tailMap = schemaMap.tailMap(packageName, true);

        List<Schema> ret = new ArrayList<>();
        for (Map.Entry<String, Schema> entry : tailMap.entrySet()) {
            String tailKey = entry.getKey();
            if (!tailKey.startsWith(packageName)) {
                break;
            }
            ret.add(entry.getValue());
        }
        return ret;
    }
}
