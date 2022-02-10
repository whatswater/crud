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

import java.util.List;
import java.util.Map;

public class JoinedTable implements Table {
    private Table left;
    private Table right;
    private JoinType joinType;
    private BoolExpression joinCondition;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public JoinedTable(Table left, Table right, JoinType joinType, BoolExpression joinCondition) {
        checkAlias(left);
        checkAlias(right);
        this.left = left;
        this.right = right;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
    }

    @Override
    public boolean isSqlQuery() {
        return false;
    }

    @Override
    public Table where(BoolExpression where) {
        ComplexTable complexTable = new ComplexTable(this);
        return complexTable.where(where);
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        ComplexTable complexTable = new ComplexTable(this);
        return complexTable.select(selectList);
    }

    @Override
    public Table distinct(boolean distinct) {
        ComplexTable complexTable = new ComplexTable(this);
        return complexTable.distinct(distinct);
    }

    @Override
    public Table limit(Limit limit) {
        ComplexTable complexTable = new ComplexTable(this);
        return complexTable.limit(limit);
    }

    @Override
    public Table orderBy(List<OrderByElement> orderByElementList) {
        ComplexTable complexTable = new ComplexTable(this);
        return complexTable.orderBy(orderByElementList);
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> new ComplexTable(this).groupBy(groupBy).select(having, selectList);
    }

    @Override
    public AliasTable<?> findMatchedTable(Table table, String columnName) {
        AliasTable<?> r1 = left.findMatchedTable(table, columnName);
        AliasTable<?> r2 = right.findMatchedTable(table, columnName);
        if (r1 == null && r2 == null) {
            return null;
        }

        if (r1 != null) {
            if (left instanceof AliasTable) {
                return (AliasTable<?>) left;
            } else {
                return r1;
            }
        } else {
            if (right instanceof AliasTable) {
                return (AliasTable<?>) right;
            } else {
                return r2;
            }
        }
    }

    @Override
    public AliasTable<?> findMatchedTable(Table table, AliasPlaceholder columnName) {
        AliasTable<?> r1 = left.findMatchedTable(table, columnName);
        AliasTable<?> r2 = right.findMatchedTable(table, columnName);
        if (r1 == null && r2 == null) {
            return null;
        }
        if (r1 != null && r2 != null) {
            throw new RuntimeException("重名");
        }

        if (r1 != null) {
            if (left instanceof AliasTable) {
                return (AliasTable<?>) left;
            } else {
                return r1;
            }
        } else {
            if (right instanceof AliasTable) {
                return (AliasTable<?>) right;
            } else {
                return r2;
            }
        }
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        left.visitAliasHolder(handler);
        right.visitAliasHolder(handler);
        joinCondition.visitAliasHolder(handler);
    }

    @Override
    public Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        ReBindReferenceExpressionVisitor visitor = new ReBindReferenceExpressionVisitor(this);
        visitor.visit(joinCondition);
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

    public Table getLeft() {
        return left;
    }

    public Table getRight() {
        return right;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public BoolExpression getJoinCondition() {
        return joinCondition;
    }

    private static void checkAlias(Table table) {
        if (table instanceof AliasTable) {
            AliasTable<?> ref = (AliasTable<?>) table;
            if (ref.getPlaceHolder() == null) {
                throw new RuntimeException("XXXX");
            }
        }
    }
}
