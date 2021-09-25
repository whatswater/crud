package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.statement.OrderByElement;

import java.util.List;

public class JoinedTable implements Table {
    private Table left;
    private Table right;
    private JoinType joinType;
    private BoolExpression joinCondition;

    public JoinedTable(Table left, Table right, JoinType joinType, BoolExpression joinCondition) {
        this.left = left;
        this.right = right;
        this.joinType = joinType;
        this.joinCondition = joinCondition;
    }

    @Override
    public Table where(BoolExpression where) {
        ComplexTable complexTable = new ComplexTable(this);
        complexTable.setWhere(where);
        return complexTable;
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        ComplexTable complexTable = new ComplexTable(this);
        complexTable.setSelectList(selectList);
        return complexTable;
    }

    @Override
    public Table orderBy(List<OrderByElement> orderByElementList) {
        ComplexTable complexTable = new ComplexTable(this);
        complexTable.setOrderBy(orderByElementList);
        return complexTable;
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> {
            ComplexTable complexTable = new ComplexTable(this);
            complexTable.setSelectList(selectList);
            complexTable.setGroupBy(groupBy);
            complexTable.setHaving(having);

            return complexTable;
        };
    }

    @Override
    public Table newAlias(AliasPlaceholder aliasPlaceholder) {
        throw new UnsupportedOperationException("JoinedTable not support alias");
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
}
