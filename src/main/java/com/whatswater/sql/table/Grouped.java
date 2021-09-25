package com.whatswater.sql.table;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.statement.SelectColumn;

import java.util.Arrays;
import java.util.List;

public interface Grouped {
    Table select(BoolExpression boolExpression, List<SelectColumn> selectList);

    default Table select(BoolExpression boolExpression, SelectColumn... selectList) {
        return select(boolExpression, Arrays.asList(selectList));
    }


    default Table select(List<SelectColumn> selectList) {
        return select(null, selectList);
    }

    default Table select(SelectColumn... selectList) {
        return select(Arrays.asList(selectList));
    }
}
