package com.whatswater.nothing.schema;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.table.Table;

import java.util.Map;

public interface Schema {
    Table getBasicTable();
    BoolExpression getQueryCondition(String queryName, Object param);
    Table toQueryParamTable(Map<String, Object> queryParam);
}
