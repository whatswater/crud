package com.whatswater.orm.action;

import com.whatswater.orm.schema.Schema;

public class Update<P, T> implements Action {
    private Schema schema;
    private P primaryKey;
    private T data;
}
