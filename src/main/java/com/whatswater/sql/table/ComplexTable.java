package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;

public class ComplexTable implements AliasTable<ComplexTable> {
    private final Table innerTable;
    private BoolExpression where;
    private boolean distinct;
    private List<SelectColumn> selectList;
    private List<Expression> groupBy;
    private BoolExpression having;
    private Limit limit;
    private List<OrderByElement> orderBy;
    private final AliasPlaceholder aliasPlaceholder;

    public ComplexTable(Table innerTable) {
        checkAlias(innerTable);
        this.innerTable = innerTable;
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public ComplexTable(
        Table innerTable,
        BoolExpression where,
        boolean distinct,
        List<SelectColumn> selectList,
        List<Expression> groupBy,
        BoolExpression having,
        List<OrderByElement> orderBy,
        Limit limit,
        AliasPlaceholder aliasPlaceholder
    ) {
        checkAlias(innerTable);
        this.innerTable = innerTable;
        this.distinct = distinct;
        this.where = where;
        this.selectList = selectList;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.limit = limit;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    @Override
    public Table where(BoolExpression where) {
        this.where = where;
        return this;
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        this.selectList = selectList;
        return this;
    }

    @Override
    public Table distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    @Override
    public Table limit(Limit limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Table orderBy(List<OrderByElement> orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> {
            this.groupBy = groupBy;
            this.having = having;
            return this.select(selectList);
        };
    }

    @Override
    public ComplexTable newAlias(AliasPlaceholder aliasPlaceholder) {
        return new ComplexTable(innerTable, where, distinct, selectList, groupBy, having, orderBy, limit, aliasPlaceholder);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public String getAliasOrTableName() {
        if (aliasPlaceholder != null && aliasPlaceholder.hasName()) {
            return aliasPlaceholder.getAlias();
        }
        return StringUtils.EMPTY;
    }

    public Table getInnerTable() {
        return innerTable;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Limit getLimit() {
        return limit;
    }

    public BoolExpression getWhere() {
        return where;
    }

    public List<SelectColumn> getSelectList() {
        return selectList;
    }

    public List<Expression> getGroupBy() {
        return groupBy;
    }

    public BoolExpression getHaving() {
        return having;
    }

    public List<OrderByElement> getOrderBy() {
        return orderBy;
    }

    private static void checkAlias(Table table) {
        if (table instanceof AliasTable) {
            AliasTable ref = (AliasTable) table;
            if (ref.getPlaceHolder() == null) {
                throw new RuntimeException("XXXX");
            }
        }
    }
}
