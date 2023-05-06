package com.whatswater.orm.state;

import com.whatswater.orm.action.Action;
import com.whatswater.orm.action.ActionContext;

public interface SchemaListener {
    void onAction(ActionContext context, Action action);
}
