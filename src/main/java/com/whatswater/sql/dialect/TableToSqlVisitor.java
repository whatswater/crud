package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class TableToSqlVisitor extends ToSqlVisitor implements TableVisitor {
    private ExpressionSqlVisitor expressionSqlVisitor;

    public TableToSqlVisitor(ExpressionSqlVisitor expressionSqlVisitor) {
        super();
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    public TableToSqlVisitor(List<Object> params, ExpressionSqlVisitor expressionSqlVisitor) {
        super(params);
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    public TableToSqlVisitor(StringBuilder sql, List<Object> params, ExpressionSqlVisitor expressionSqlVisitor) {
        super(sql, params);
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    @Override
    public void visit(DbTable<?> table) {
        sql.append(table.getTableName());
        if (table.hasAlias()) {
            sql.append(" ").append(table.getPlaceHolder().getAlias());
        }
    }

    @Override
    public void visit(SelectedTable table) {
        sql.append("select ");
        if (table.isDistinct()) {
            sql.append(" distinct");
        }

        List<SelectColumn> selectColumnList = table.getSelectList();
        SelectColumnToSqlVisitor selectColumnVisitor = new SelectColumnToSqlVisitor(expressionSqlVisitor);
        for (SelectColumn column: selectColumnList) {
            selectColumnVisitor.clearAll();
            SelectColumnVisitor.visit(column, selectColumnVisitor);
            sql.append(selectColumnVisitor.getSql()).append(", ");
            params.addAll(selectColumnVisitor.getParams());
        }
        sql.replace(sql.length() - 2, sql.length(), "");
        sql.append(" from ");
        DbTable<?> dbTable = table.getRawTable();
        this.visit(dbTable);

        BoolExpression where = table.getWhere();
        if (where != null) {
            sql.append(" where ");
            expressionSqlVisitor.clearAll();
            expressionSqlVisitor.visit(where);
            sql.append(expressionSqlVisitor.getSql());
            params.addAll(expressionSqlVisitor.getParams());
        }

        List<OrderByElement> orderByElements = table.getOrderBy();
        if (CollectionUtils.isNotEmpty(orderByElements)) {
            sql.append(" order by ");
            for (OrderByElement orderByElement: orderByElements) {
                Expression expression = orderByElement.getExpression();
                expressionSqlVisitor.clearAll();
                expressionSqlVisitor.visit(expression);
                sql.append(expressionSqlVisitor.getSql());
                params.addAll(expressionSqlVisitor.getParams());
                sql.append(", ");
            }
            sql.replace(sql.length() - 2, sql.length(), "");
        }

        appendLimit(table.getLimit());
        System.out.println(sql);
    }

    @Override
    public void visit(JoinedTable table) {
        Table left = table.getLeft();
        if (!(left instanceof DbTable)) {
            sql.append("(");
            TableVisitor.visit(left, this);
            sql.append(")");
        } else {
            TableVisitor.visit(left, this);
        }

        JoinType joinType = table.getJoinType();
        switch (joinType) {
            case inner:
                sql.append(" inner join ");
                break;
            case left:
                sql.append(" left join ");
                break;
            case right:
                sql.append(" right join ");
                break;
            case full:
                sql.append(" full outer join ");
                break;
        }

        Table right = table.getRight();
        if (!(right instanceof DbTable)) {
            sql.append("(");
            TableVisitor.visit(right, this);
            sql.append(")");
        } else {
            TableVisitor.visit(right, this);
        }
    }

    @Override
    public void visit(ComplexTable table) {
        sql.append("select ");
        if (table.isDistinct()) {
            sql.append(" distinct");
        }

        List<SelectColumn> selectColumnList = table.getSelectList();
        SelectColumnToSqlVisitor selectColumnVisitor = new SelectColumnToSqlVisitor(expressionSqlVisitor);
        for (SelectColumn column: selectColumnList) {
            selectColumnVisitor.clearAll();
            SelectColumnVisitor.visit(column, selectColumnVisitor);
            sql.append(selectColumnVisitor.getSql()).append(", ");
            params.addAll(selectColumnVisitor.getParams());
        }
        sql.replace(sql.length() - 2, sql.length(), "");
        sql.append(" from ");
        Table innerTable = table.getInnerTable();
        TableVisitor.visit(innerTable, this);

        BoolExpression where = table.getWhere();
        if (where != null) {
            sql.append(" where ");
            expressionSqlVisitor.clearAll();
            expressionSqlVisitor.visit(where);
            sql.append(expressionSqlVisitor.getSql());
            params.addAll(expressionSqlVisitor.getParams());
        }

        List<Expression> groupBy = table.getGroupBy();
        if (CollectionUtils.isNotEmpty(groupBy)) {
            sql.append(" group by ");
            for (Expression expression: groupBy) {
                expressionSqlVisitor.clearAll();
                expressionSqlVisitor.visit(expression);
                sql.append(expressionSqlVisitor.getSql());
                params.addAll(expressionSqlVisitor.getParams());
                sql.append(", ");
            }
            sql.replace(sql.length() - 2, sql.length(), "");
        }

        BoolExpression having = table.getHaving();
        if (having != null) {
            sql.append(" having ");
            expressionSqlVisitor.clearAll();
            expressionSqlVisitor.visit(having);
            sql.append(expressionSqlVisitor.getSql());
            params.addAll(expressionSqlVisitor.getParams());
        }

        List<OrderByElement> orderByElements = table.getOrderBy();
        if (CollectionUtils.isNotEmpty(orderByElements)) {
            sql.append(" order by ");
            for (OrderByElement orderByElement: orderByElements) {
                Expression expression = orderByElement.getExpression();
                expressionSqlVisitor.clearAll();
                expressionSqlVisitor.visit(expression);
                sql.append(expressionSqlVisitor.getSql());
                params.addAll(expressionSqlVisitor.getParams());
                if (!orderByElement.isAsc()) {
                    sql.append(" desc");
                }
                sql.append(", ");
            }
            sql.replace(sql.length() - 2, sql.length(), "");
        }
        appendLimit(table.getLimit());
    }

    private void appendLimit(Limit limit) {
        if (limit != null) {
            sql.append(" limit ");
            if (limit.getOffset() > 0) {
                sql.append(limit.getOffset()).append(", ");
            }
            sql.append(limit.getSize());
        }
    }
}
