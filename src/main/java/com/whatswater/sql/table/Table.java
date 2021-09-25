package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.statement.Query;

import java.util.Arrays;
import java.util.List;


public interface Table {
    default <M> Query<M> toQuery(ResultMapper<M> mapper) {
        return new Query<>(this, mapper);
    }
    default <M> Query<M> toQuery(ResultMapper<M> mapper, Limit limit) {
        return new Query<>(this, mapper, limit);
    }

    default JoinedTable join(Table right, BoolExpression boolExpression, JoinType joinType) {
        return new JoinedTable(this, right, joinType, boolExpression);
    }
    default JoinedTable join(Table right, BoolExpression boolExpression) {
        return join(right, boolExpression, JoinType.inner);
    }
    default JoinedTable leftJoin(Table right, BoolExpression boolExpression) {
        return join(right, boolExpression, JoinType.left);
    }

    Table where(BoolExpression where);
    Table select(List<SelectColumn> selectList);
    default Table select(SelectColumn... selectArr) {
        return select(Arrays.asList(selectArr));
    }

    Table orderBy(List<OrderByElement> orderByElementList);
    default Table orderBy(OrderByElement... orderByElementArr) {
        return orderBy(Arrays.asList(orderByElementArr));
    }

    Grouped groupBy(List<Expression> groupBy);
    default Grouped groupBy(Expression... expressionArr) {
        return groupBy(Arrays.asList(expressionArr));
    }

    Table newAlias(AliasPlaceholder aliasPlaceholder);
    default Table newAlias() {
        return newAlias(new AliasPlaceholder());
    }
}
