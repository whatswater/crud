package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.dialect.Dialect.SQL;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;

public class SelectColumnToSqlVisitor implements SelectColumnVisitor {
    private ExpressionSqlVisitor expressionSqlVisitor;
    private SQL sql;

    public SelectColumnToSqlVisitor(SQL sql, ExpressionSqlVisitor expressionSqlVisitor) {
        this.expressionSqlVisitor = expressionSqlVisitor;
        this.sql = sql;
    }

    @Override
    public void visit(AliasColumnReference colRef) {
        sql.append(Expression.toSQL(expressionSqlVisitor, colRef));
    }

    @Override
    public void visit(Alias alias) {
        sql.append(Expression.toSQL(expressionSqlVisitor, alias.getExpression()))
            .append(" ")
            .append(alias.getAliasPlaceholder().getAlias());
    }

    @Override
    public void visit(RawColumnReference colRef) {
        sql.append(Expression.toSQL(expressionSqlVisitor, colRef));
    }

    public SQL getSql() {
        return sql;
    }
}
