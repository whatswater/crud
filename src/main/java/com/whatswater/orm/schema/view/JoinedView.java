package com.whatswater.orm.schema.view;

import com.whatswater.orm.schema.Schema;

public class JoinedView<T1, T2> {
    private Schema<T1> leftSchema;
    private Schema<T2> rightSchema;
}
