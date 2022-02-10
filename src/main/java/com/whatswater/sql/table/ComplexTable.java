package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.dialect.ReBindReferenceExpressionVisitor;
import com.whatswater.sql.dialect.ReBindTableSelectColumnVisitor;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.utils.CollectionUtils;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;
import java.util.Map;

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
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

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
    public boolean isSqlQuery() {
        return true;
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
    public AliasTable<?> findMatchedTable(Table table, String columnName) {
        if (CollectionUtils.isEmpty(selectList)) {
            AliasTable<?> r = innerTable.findMatchedTable(table, columnName);
            if (r != null) {
                if (innerTable instanceof AliasTable) {
                    return (AliasTable<?>) innerTable;
                } else {
                    return r;
                }
            }
            return null;
        }
        if (table == this) {
            // 只有直接引用table时，才判断当前表的列
            for (SelectColumn selectColumn: selectList) {
                if (selectColumn.matchColumnName(columnName)) {
                    return this;
                }
            }
        }
        return null;
    }

    @Override
    public AliasTable<?> findMatchedTable(Table table, AliasPlaceholder columnName) {
        if (CollectionUtils.isEmpty(selectList)) {
            AliasTable<?> r = innerTable.findMatchedTable(table, columnName);
            if (r != null) {
                if (innerTable instanceof AliasTable) {
                    return (AliasTable<?>) innerTable;
                } else {
                    return r;
                }
            }
            return null;
        }
        if (table == this) {
            // 只有直接引用table时，才判断当前表的列
            for (SelectColumn selectColumn: selectList) {
                if (selectColumn.matchColumnName(columnName)) {
                    return this;
                }
            }
        }
        return null;
    }

    @Override
    public ComplexTable newAlias(AliasPlaceholder aliasPlaceholder) {
        return new ComplexTable(innerTable, where, distinct, selectList, groupBy, having, orderBy, limit, aliasPlaceholder);
    }

    @Override
    public boolean hasAlias() {
        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
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

    @Override
    public void visitAliasHolder(Handler handler) {
        innerTable.visitAliasHolder(handler);
        if (selectList != null) {
            for (SelectColumn selectColumn: selectList) {
                selectColumn.visitAliasHolder(handler);
            }
        }
        if (where != null) {
            where.visitAliasHolder(handler);
        }
        if (orderBy != null) {
            for (OrderByElement orderByElement: orderBy) {
                orderByElement.visitAliasHolder(handler);
            }
        }
        if (groupBy != null) {
            for (Expression groupByItem: groupBy) {
                groupByItem.visitAliasHolder(handler);
            }
        }
        if (having != null) {
            having.visitAliasHolder(handler);
        }
        handler.handle(this.aliasPlaceholder);
    }

    @Override
    public Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        if (CollectionUtils.isNotEmpty(selectList)) {
            ReBindTableSelectColumnVisitor visitor = new ReBindTableSelectColumnVisitor(innerTable);
            for (SelectColumn selectColumn: selectList) {
                visitor.visit(selectColumn);
            }
            mergeSymbolReplaceMap(visitor.getSymbolReplaceMap());
        }
        ReBindReferenceExpressionVisitor visitor = new ReBindReferenceExpressionVisitor(innerTable);
        if (where != null) {
            visitor.visit(where);
        }
        if (CollectionUtils.isNotEmpty(groupBy)) {
            for (Expression expression: groupBy) {
                visitor.visit(expression);
            }
        }
        if (having != null) {
            visitor.visit(having);
        }
        if (orderBy != null) {
            for (OrderByElement orderByElement: orderBy) {
                visitor.visit(orderByElement.getExpression());
            }
        }
        mergeSymbolReplaceMap(visitor.getSymbolReplaceMap());
        return symbolReplaceMap;
    }

    private void mergeSymbolReplaceMap(Map<ReferenceExpression, ReferenceExpression> from) {
        if (from == null) {
            return;
        }

        if (symbolReplaceMap == null) {
            this.symbolReplaceMap = from;
        }
        this.symbolReplaceMap.putAll(from);
    }
}
