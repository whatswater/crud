package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;

import java.util.List;

public class SelectColumnToSqlVisitor extends ToSqlVisitor implements SelectColumnVisitor {
    private ExpressionSqlVisitor expressionSqlVisitor;

    public SelectColumnToSqlVisitor(ExpressionSqlVisitor expressionSqlVisitor) {
        super();
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    public SelectColumnToSqlVisitor(List<Object> params, ExpressionSqlVisitor expressionSqlVisitor) {
        super(params);
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    public SelectColumnToSqlVisitor(StringBuilder sql, List<Object> params, ExpressionSqlVisitor expressionSqlVisitor) {
        super(sql, params);
        this.expressionSqlVisitor = expressionSqlVisitor;
    }

    @Override
    public void visit(AliasColumnReference colRef) {
        sql.append(colRef.getTable().getAliasOrTableName()).append(".").append(colRef.getAliasOrColumnName());
    }

    @Override
    public void visit(Alias alias) {
        Expression expression = alias.getExpression();

        expressionSqlVisitor.clearAll();
        expressionSqlVisitor.visit(expression);
        params.addAll(expressionSqlVisitor.getParams());
        sql.append(expressionSqlVisitor.getAndClearSql()).append(" ").append(alias.getAliasOrColumnName());
    }

    @Override
    public void visit(RawColumnReference colRef) {
        sql.append(colRef.getTable().getAliasOrTableName()).append(".").append(colRef.getAliasOrColumnName());
    }
}
