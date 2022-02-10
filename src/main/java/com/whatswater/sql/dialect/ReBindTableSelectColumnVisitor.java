package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.table.Table;

import java.util.*;


public class ReBindTableSelectColumnVisitor implements SelectColumnVisitor {
    private final Table innerTable;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public ReBindTableSelectColumnVisitor(Table newTable) {
        this.innerTable = newTable;
    }

    @Override
    public void visit(AliasColumnReference aliasColumnReference) {
        AliasTable<?> table = aliasColumnReference.getTable();
        AliasTable<?> thisScopeTable = innerTable.findMatchedTable(table, aliasColumnReference.getColumnAlias());
        if (thisScopeTable == null) {
            throw new RuntimeException("refTable null");
        }

        if (thisScopeTable != table) {
            addSymbolReplace(aliasColumnReference, aliasColumnReference.bindNewTable(thisScopeTable));
        }
    }

    @Override
    public void visit(Alias alias) {
        Expression expression = alias.getExpression();
        ReBindReferenceExpressionVisitor reBindReferenceExpressionVisitor = new ReBindReferenceExpressionVisitor(innerTable);
        reBindReferenceExpressionVisitor.visit(expression);
        if (Objects.nonNull(reBindReferenceExpressionVisitor.getSymbolReplaceMap())) {
            if (symbolReplaceMap == null) {
                symbolReplaceMap = reBindReferenceExpressionVisitor.getSymbolReplaceMap();
            } else {
                symbolReplaceMap.putAll(reBindReferenceExpressionVisitor.getSymbolReplaceMap());
            }
        }
    }

    @Override
    public void visit(RawColumnReference rawColumnReference) {
        AliasTable<?> table = rawColumnReference.getTable();
        AliasTable<?> thisScopeTable = innerTable.findMatchedTable(table, rawColumnReference.getColumnName());
        if (thisScopeTable == null) {
            throw new RuntimeException("refTable null");
        }

        if (thisScopeTable != table) {
            addSymbolReplace(rawColumnReference, rawColumnReference.bindNewTable(thisScopeTable));
        }
    }

    public void addSymbolReplace(ReferenceExpression oldExpression, ReferenceExpression newExpression) {
        if (symbolReplaceMap == null) {
            symbolReplaceMap = new HashMap<>();
        }
        symbolReplaceMap.put(oldExpression, newExpression);
    }

    public Map<ReferenceExpression, ReferenceExpression> getSymbolReplaceMap() {
        return symbolReplaceMap;
    }
}
