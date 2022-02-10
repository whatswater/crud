package com.whatswater.curd.project.common;


import com.zandero.rest.reader.ValueReader;
import com.zandero.utils.StringUtils;
import com.zandero.utils.extra.JsonUtils;

public class CrudJsonValueReader extends ObjectMapperHolder implements ValueReader<Object> {


    @Override
    public Object read(String value, Class<Object> type) throws Throwable {
        if (StringUtils.isNullOrEmptyTrimmed(value)) {
            return null;
        }

        return JsonUtils.fromJson(value, type, mapper);
    }
}
