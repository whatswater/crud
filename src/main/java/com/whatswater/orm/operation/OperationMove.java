package com.whatswater.orm.operation;

import com.whatswater.orm.schema.Schema;

public class OperationMove {
    Schema parentSchema;
    Schema from;
    Schema to;

    Object data;
    int updateCount;
}
