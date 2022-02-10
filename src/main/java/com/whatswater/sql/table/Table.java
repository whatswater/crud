package com.whatswater.sql.table;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.statement.Query;
import org.checkerframework.checker.units.qual.A;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public interface Table extends AliasHolderVisitor {
    default <M> Query<M> toQuery(ResultMapper<M> mapper) {
        return new Query<>(this, mapper);
    }
    default <M> Query<M> toQuery(ResultMapper<M> mapper, Limit limit) {
        return new Query<>(this.limit(limit), mapper);
    }

    /**
     * 是否可以作为一个SQL查询
     * @return 是否是sql查询
     */
    boolean isSqlQuery();

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
    default Table select(Expression expression, AliasPlaceholder aliasPlaceholder) {
        Alias alias = new Alias(expression, aliasPlaceholder);
        return select(alias);
    }
    default Table select(AliasTable<?> table, AliasPlaceholder aliasPlaceholder) {
        AliasColumnReference aliasColumnReference = new AliasColumnReference(table, aliasPlaceholder);
        return select(aliasColumnReference);
    }


    Table distinct(boolean distinct);
    default Table distinct() {
        return this.distinct(true);
    }

    Table limit(Limit limit);
    default Table limit(int offset, int size) {
        return limit(new Limit(offset, size));
    }
    default Table limit(int size) {
        return limit(new Limit(size));
    }

    Table orderBy(List<OrderByElement> orderByElementList);
    default Table orderBy(OrderByElement... orderByElementArr) {
        return orderBy(Arrays.asList(orderByElementArr));
    }
    default Table orderBy(Expression expression) {
        return orderBy(new OrderByElement(expression, true));
    }
    default Table orderByDesc(Expression expression) {
        return orderBy(new OrderByElement(expression, false));
    }

    Grouped groupBy(List<Expression> groupBy);
    default Grouped groupBy(Expression... expressionArr) {
        return groupBy(Arrays.asList(expressionArr));
    }

    /**
     * 判断当前table内部是否存在某一列
     * @param table 列的原始引用table
     * @param columnName 列名
     * @return 当前table内部持有这一列的内部table
     */
    AliasTable<?> findMatchedTable(Table table, String columnName);

    /**
     * 判断当前table内部是否存在某一列，不存在时返回null
     * @param table 列的原始引用table
     * @param aliasPlaceholder 列别名
     * @return 当前table内部持有这一列的内部table
     */
    AliasTable<?> findMatchedTable(Table table, AliasPlaceholder aliasPlaceholder);
    default Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        return null;
    }
}
