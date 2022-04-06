package com.whatswater.curd.project.template;


import io.vertx.core.Future;

import java.util.List;
import java.util.Map;

public class BaseModelService {
    public Future<BaseModelSearchResult> search(BaseModel baseModel, Map<String, Object> queryParam) {
        List<BaseModelProperty> properties = baseModel.getProperties();
        // todo 生成Query对象
        // todo 根据Property生成对象
        return Future.succeededFuture();
    }
}
