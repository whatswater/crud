package com.whatswater.orm.field;

import com.whatswater.orm.action.ActionContext;

public interface ComputeFunction {
    Object compute(ActionContext context, FieldGetter getter);
}
