package com.whatswater.sql.statement;


import com.whatswater.sql.dialect.ReBindReferenceExpressionVisitor;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinedTable;
import com.whatswater.sql.table.Table;

import java.util.Map;

// 只支持构建单表删除语句，暂不支持多表删除
public class Delete {
    private final DbTable<?> dbTable;
    private BoolExpression where;
    private Limit limit;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public Delete(DbTable<?> dbTable) {
        this.dbTable = dbTable;
    }

    public Delete(DbTable<?> dbTable, BoolExpression where) {
        this.dbTable = dbTable;
        this.where = where;
    }

    public Delete(DbTable<?> dbTable, BoolExpression where, Limit limit) {
        this.dbTable = dbTable;
        this.where = where;
        this.limit = limit;
    }

    public Delete where(BoolExpression where) {
        this.where = where;
        return this;
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public BoolExpression getWhere() {
        return where;
    }

    public Limit getLimit() {
        return limit;
    }

    public Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        ReBindReferenceExpressionVisitor visitor = new ReBindReferenceExpressionVisitor(dbTable);
        if (where != null) {
            visitor.visit(where);
            mergeSymbolReplaceMap(visitor.getSymbolReplaceMap());
        }
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
