package com.whatswater.nothing.schema;


import com.whatswater.sql.expression.BoolExpression;

public interface QueryConditionBuilder {
    BoolExpression queryCondition(String queryName, Object param);
}
