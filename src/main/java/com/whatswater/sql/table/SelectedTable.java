package com.whatswater.sql.table;

import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;

public class SelectedTable implements AliasTable<SelectedTable> {
    private final DbTable<?> rawTable;
    private boolean distinct;
    private List<SelectColumn> selectList;
    private BoolExpression where;
    private Limit limit;
    private List<OrderByElement> orderBy;
    private final AliasPlaceholder aliasPlaceholder;

    public SelectedTable(DbTable<?> rawTable) {
        checkAlias(rawTable);
        this.rawTable = rawTable;
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public SelectedTable(DbTable<?> rawTable, AliasPlaceholder aliasPlaceholder) {
        checkAlias(rawTable);
        this.rawTable = rawTable;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    public SelectedTable(DbTable<?> rawTable, List<SelectColumn> selectList, BoolExpression where, List<OrderByElement> orderBy, AliasPlaceholder aliasPlaceholder) {
        checkAlias(rawTable);
        this.rawTable = rawTable;
        this.selectList = selectList;
        this.where = where;
        this.orderBy = orderBy;
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
        return (having, selectList) -> new ComplexTable(rawTable)
            .where(where)
            .orderBy(orderBy)
            .groupBy(groupBy).select(having, selectList);
    }

    @Override
    public SelectedTable newAlias(AliasPlaceholder aliasPlaceholder) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    public boolean hasAlias() {
        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }

    public DbTable<?> getRawTable() {
        return rawTable;
    }

    public List<SelectColumn> getSelectList() {
        return selectList;
    }

    public BoolExpression getWhere() {
        return where;
    }

    public List<OrderByElement> getOrderBy() {
        return orderBy;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Limit getLimit() {
        return limit;
    }

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return StringUtils.EMPTY;
    }

    private static void checkAlias(DbTable<?> table) {
        if (table.getPlaceHolder() == null) {
            throw new RuntimeException("XXXX");
        }
    }
}
