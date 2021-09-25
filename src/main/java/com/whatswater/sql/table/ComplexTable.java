package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.alias.AliasPlaceholderGetter;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;

public class ComplexTable implements TableCanRef, AliasPlaceholderGetter {
    private final Table innerTable;
    private BoolExpression where;
    private List<SelectColumn> selectList;
    private List<Expression> groupBy;
    private BoolExpression having;
    private List<OrderByElement> orderBy;
    private final AliasPlaceholder aliasPlaceholder;

    public ComplexTable(Table innerTable) {
        this.innerTable = innerTable;
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public ComplexTable(Table innerTable, BoolExpression where, List<SelectColumn> selectList, List<Expression> groupBy, BoolExpression having, List<OrderByElement> orderBy, AliasPlaceholder aliasPlaceholder) {
        this.innerTable = innerTable;
        this.where = where;
        this.selectList = selectList;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    @Override
    public Table where(BoolExpression where) {
        return new ComplexTable(innerTable, where, selectList, groupBy, having, orderBy, aliasPlaceholder);
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        return new ComplexTable(innerTable, where, selectList, groupBy, having, orderBy, aliasPlaceholder);
    }

    @Override
    public Table orderBy(List<OrderByElement> orderBy) {
        return new ComplexTable(innerTable, where, selectList, groupBy, having, orderBy, aliasPlaceholder);
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> {
            ComplexTable complexTable = new ComplexTable(innerTable);
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
        return new ComplexTable(innerTable, where, selectList, groupBy, having, orderBy, aliasPlaceholder);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public boolean hasAlias() {
        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return StringUtils.EMPTY;
    }

    void setWhere(BoolExpression where) {
        this.where = where;
    }

    void setSelectList(List<SelectColumn> selectList) {
        this.selectList = selectList;
    }

    void setGroupBy(List<Expression> groupBy) {
        this.groupBy = groupBy;
    }

    void setHaving(BoolExpression having) {
        this.having = having;
    }

    void setOrderBy(List<OrderByElement> orderBy) {
        this.orderBy = orderBy;
    }

    public Table getInnerTable() {
        return innerTable;
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

    public AliasPlaceholder getAliasPlaceholder() {
        return aliasPlaceholder;
    }
}
