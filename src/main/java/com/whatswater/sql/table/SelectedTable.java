package com.whatswater.sql.table;

import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.alias.AliasPlaceholderGetter;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;

public class SelectedTable implements TableCanRef, AliasPlaceholderGetter {
    private final DbTable<?> rawTable;
    private List<SelectColumn> selectList;
    private BoolExpression where;
    private List<OrderByElement> orderBy;
    private final AliasPlaceholder aliasPlaceholder;

    public SelectedTable(DbTable<?> rawTable) {
        this.rawTable = rawTable;
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public SelectedTable(DbTable<?> rawTable, AliasPlaceholder aliasPlaceholder) {
        this.rawTable = rawTable;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    public SelectedTable(DbTable<?> rawTable, List<SelectColumn> selectList, BoolExpression where, List<OrderByElement> orderBy, AliasPlaceholder aliasPlaceholder) {
        this.rawTable = rawTable;
        this.selectList = selectList;
        this.where = where;
        this.orderBy = orderBy;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    @Override
    public Table where(BoolExpression where) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
    }

    @Override
    public Table orderBy(List<OrderByElement> orderBy) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> {
            ComplexTable complexTable = new ComplexTable(rawTable);
            complexTable.setWhere(where);
            complexTable.setSelectList(selectList);
            complexTable.setOrderBy(orderBy);
            complexTable.setGroupBy(groupBy);
            complexTable.setHaving(having);

            return complexTable;
        };
    }

    @Override
    public Table newAlias(AliasPlaceholder aliasPlaceholder) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
    }

    void setSelectList(List<SelectColumn> selectList) {
        this.selectList = selectList;
    }

    void setWhere(BoolExpression where) {
        this.where = where;
    }

    void setOrderBy(List<OrderByElement> orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
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

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return StringUtils.EMPTY;
    }
}
