package com.whatswater.orm.action;

import com.whatswater.orm.schema.Schema;

import java.util.Collections;
import java.util.List;

public class Insert<T, R> implements Action<R> {
    private final Schema<T> schema;
    private List<Object[]> arrDataList;
    private List<T> dataList;

    public Insert(Schema<T> schema, Object[] data) {
        this.schema = schema;
        this.arrDataList = Collections.singletonList(data);
    }

    public Insert(Schema<T> schema, T data) {
        this.schema = schema;
        this.dataList = Collections.singletonList(data);
    }
}
