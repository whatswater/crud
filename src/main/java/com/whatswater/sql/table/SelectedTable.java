package com.whatswater.sql.table;

import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.dialect.ReBindReferenceExpressionVisitor;
import com.whatswater.sql.dialect.ReBindTableSelectColumnVisitor;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.utils.CollectionUtils;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class SelectedTable implements AliasTable<SelectedTable>, AliasHolderVisitor {
    private final DbTable<?> rawTable;
    private boolean distinct;
    private List<SelectColumn> selectList;
    private BoolExpression where;
    private Limit limit;
    private List<OrderByElement> orderBy;
    private final AliasPlaceholder aliasPlaceholder;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public SelectedTable(DbTable<?> rawTable) {
        this(rawTable, new AliasPlaceholder());
    }

    public SelectedTable(DbTable<?> rawTable, AliasPlaceholder aliasPlaceholder) {
        this(rawTable, null, null, null, aliasPlaceholder);
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
        return (having, selectList) -> new ComplexTable(rawTable)
            .where(where)
            .orderBy(orderBy)
            .groupBy(groupBy).select(having, selectList);
    }

    @Override
    public AliasTable<?> findMatchedTable(Table table, String columnName) {
        if (CollectionUtils.isEmpty(selectList)) {
            Table r1 = rawTable.findMatchedTable(table, columnName);
            if (r1 != null) {
                return rawTable;
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
            Table r1 = rawTable.findMatchedTable(table, columnName);
            if (r1 != null) {
                return rawTable;
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
    public SelectedTable newAlias(AliasPlaceholder aliasPlaceholder) {
        return new SelectedTable(rawTable, selectList, where, orderBy, aliasPlaceholder);
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

    @Override
    public void visitAliasHolder(AliasHolderVisitor.Handler handler) {
        rawTable.visitAliasHolder(handler);
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
        handler.handle(this.aliasPlaceholder);
    }

    @Override
    public Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        if (CollectionUtils.isNotEmpty(selectList)) {
            ReBindTableSelectColumnVisitor visitor = new ReBindTableSelectColumnVisitor(rawTable);
            for (SelectColumn selectColumn: selectList) {
                visitor.visit(selectColumn);
            }
            mergeSymbolReplaceMap(visitor.getSymbolReplaceMap());
        }
        ReBindReferenceExpressionVisitor visitor = new ReBindReferenceExpressionVisitor(rawTable);
        if (where != null) {
            visitor.visit(where);
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

    private static void checkAlias(DbTable<?> table) {
        if (table.getPlaceHolder() == null) {
            throw new RuntimeException("DbTable 没有设置别名");
        }
    }
}
