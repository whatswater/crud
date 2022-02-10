package com.whatswater.sql.dialect;


import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.LogicExpression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.Limit;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.*;
import com.whatswater.sql.utils.CollectionUtils;
import com.whatswater.sql.utils.StringUtils;

import java.util.List;
import java.util.Map;

public class TableSqlVisitor implements TableVisitor {
    private ExpressionSqlVisitor expressionVisitor;
    private SQL sql;

    public TableSqlVisitor(ExpressionSqlVisitor expressionVisitor) {
        this(expressionVisitor, new SQL());
    }

    public TableSqlVisitor(ExpressionSqlVisitor expressionVisitor, SQL sql) {
        this.expressionVisitor = expressionVisitor;
        this.sql = sql;
    }

    public SQL getSql() {
        return sql;
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

        if (CollectionUtils.isEmpty(table.getSelectList())) {
            sql.append("*");
        } else {
            List<SelectColumn> selectColumnList = table.getSelectList();
            SelectColumnToSqlVisitor selectColumnVisitor = new SelectColumnToSqlVisitor(sql, expressionVisitor);
            for (SelectColumn column: selectColumnList) {
                selectColumnVisitor.visit(column);
                sql.append(StringUtils.COMMA);
            }
            sql.deleteLastChar(StringUtils.COMMA);
        }
        sql.append(" from ");
        visit(table.getRawTable());

        BoolExpression where = table.getWhere();
        if (where != null) {
            sql.append(" where ");
            if (where instanceof LogicExpression) {
                where = ((LogicExpression) where).flatten();
            }
            sql.append(Expression.toSQL(expressionVisitor, where).removeBrackets());
        }

        List<OrderByElement> orderByElements = table.getOrderBy();
        if (CollectionUtils.isNotEmpty(orderByElements)) {
            sql.append(" order by ");
            for (OrderByElement orderByElement: orderByElements) {
                Expression expression = orderByElement.getExpression();
                sql.append(Expression.toSQL(expressionVisitor, expression)).append(StringUtils.COMMA);
            }
            sql.deleteLastChar(StringUtils.COMMA);
        }
        appendLimit(table.getLimit());
    }

    private void visitChild(Table table) {
        Map<ReferenceExpression, ReferenceExpression> symbolMap = table.reBindColumnReference();
        ExpressionSqlVisitor visitor = new ExpressionSqlVisitor(new SQL());
        visitor.setSymbolReplaceMap(symbolMap);

        TableSqlVisitor childTableSqlVisitor = new TableSqlVisitor(visitor);
        childTableSqlVisitor.sql = this.sql;
        childTableSqlVisitor.visit(table);
    }

    @Override
    public void visit(JoinedTable table) {
        Table left = table.getLeft();
        if (left instanceof DbTable) {
            visitChild(left);
        } else if (left instanceof AliasTable<?>) {
            sql.append("(");
            visitChild(left);
            sql.append(")");

            AliasTable<?> aliasTable = (AliasTable<?>) left;
            if (aliasTable.hasAlias()) {
                sql.append(" ").append(aliasTable.getPlaceHolder().getAlias());
            }
        } else {
            sql.append("(");
            visitChild(left);
            sql.append(")");
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
        if (right instanceof DbTable) {
            visitChild(right);
        } else if (right instanceof AliasTable<?>) {
            sql.append("(");
            visitChild(right);
            sql.append(")");

            AliasTable<?> aliasTable = (AliasTable<?>) right;
            if (aliasTable.hasAlias()) {
                sql.append(" ").append(aliasTable.getPlaceHolder().getAlias());
            }
        } else {
            sql.append("(");
            visitChild(right);
            sql.append(")");
        }
        sql.append(" on ").append(Expression.toSQL(expressionVisitor, table.getJoinCondition()));
    }

    @Override
    public void visit(ComplexTable table) {
        sql.append("select ");
        if (table.isDistinct()) {
            sql.append(" distinct");
        }

        if (CollectionUtils.isEmpty(table.getSelectList())) {
            sql.append("*");
        } else {
            List<SelectColumn> selectColumnList = table.getSelectList();
            SelectColumnToSqlVisitor selectColumnVisitor = new SelectColumnToSqlVisitor(sql, expressionVisitor);
            for (SelectColumn column: selectColumnList) {
                selectColumnVisitor.visit(column);
                sql.append(StringUtils.COMMA);
            }
            sql.deleteLastChar(StringUtils.COMMA);
        }

        sql.append(" from ");
        Table innerTable = table.getInnerTable();
        if (innerTable instanceof DbTable<?>) {
            visit(innerTable);
        } else if (innerTable instanceof AliasTable<?>) {
            sql.append("(");
            visit(innerTable);
            sql.append(")");

            AliasTable<?> aliasTable = (AliasTable<?>) innerTable;
            if (aliasTable.hasAlias()) {
                sql.append(" ").append(aliasTable.getPlaceHolder().getAlias());
            }
        } else {
            visit(innerTable);
        }

        BoolExpression where = table.getWhere();
        if (where != null) {
            sql.append(" where ");
            if (where instanceof LogicExpression) {
                where = ((LogicExpression) where).flatten();
            }
            sql.append(Expression.toSQL(expressionVisitor, where).removeBrackets());
        }

        List<Expression> groupBy = table.getGroupBy();
        if (CollectionUtils.isNotEmpty(groupBy)) {
            sql.append(" group by ");
            for (Expression expression: groupBy) {
                sql.append(Expression.toSQL(expressionVisitor, expression)).append(StringUtils.COMMA);
            }
            sql.deleteLastChar(StringUtils.COMMA);
        }

        BoolExpression having = table.getHaving();
        if (having != null) {
            sql.append(" having ");
            sql.append(Expression.toSQL(expressionVisitor, having));
        }

        List<OrderByElement> orderByElements = table.getOrderBy();
        if (CollectionUtils.isNotEmpty(orderByElements)) {
            sql.append(" order by ");
            for (OrderByElement orderByElement: orderByElements) {
                Expression expression = orderByElement.getExpression();
                sql.append(Expression.toSQL(expressionVisitor, expression));
                if (!orderByElement.isAsc()) {
                    sql.append(" desc");
                }
                sql.append(StringUtils.COMMA);
            }
            sql.deleteLastChar(StringUtils.COMMA);
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
