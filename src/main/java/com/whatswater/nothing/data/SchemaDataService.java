package com.whatswater.nothing.data;


import io.vertx.core.Future;

import java.io.Serializable;
import java.util.Map;

public interface SchemaDataService {
    Future<ModelDataList> list(Map<String, Object> params);
    ModelData getOne(Map<String, Object> params);
    ModelData getByPrimaryKey(Serializable primaryKey);
    ModelData page(Page page, Map<String, Object> params);
}
