package com.whatswater.orm.action;

import com.whatswater.orm.schema.Schema;

public class Move implements Action {
    private Schema from;
    private Schema to;

    private Object data;
}
