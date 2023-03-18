package com.whatswater.orm.schema.view;

import com.whatswater.orm.schema.Schema;

import java.util.List;

public class UnionView<T> {
    private List<Schema<T>> schemaList;
}
