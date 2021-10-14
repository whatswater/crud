package com.whatswater.sql.table;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.OrderByElement;

import java.util.List;

public class JoinedTable implements Table {
    private Table left;
    private Table right;
    private JoinType joinType;
    private BoolExpression joinCondition;

    public JoinedTable(Table left, Table right, JoinType joinType, BoolExpression joinCondition) {
        checkAlias(left);
        checkAlias(right);
        this.left = left;
        this.right = right;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
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
            AliasTable ref = (AliasTable) table;
            if (ref.getPlaceHolder() == null) {
                throw new RuntimeException("XXXX");
            }
        }
    }
}
