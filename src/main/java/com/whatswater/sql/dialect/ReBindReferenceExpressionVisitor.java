package com.whatswater.sql.dialect;


import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.table.Table;

import java.util.HashMap;
import java.util.Map;


public class ReBindReferenceExpressionVisitor extends ReferenceExpressionVisitor {
    private final Table innerTable;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public ReBindReferenceExpressionVisitor(Table table) {
        this.innerTable = table;
    }

    @Override
    public void visit(ReferenceExpression reference) {
        if (reference instanceof RawColumnReference) {
            RawColumnReference rawColumnReference = (RawColumnReference) reference;
            AliasTable<?> table = rawColumnReference.getTable();
            AliasTable<?> thisScopeTable = innerTable.findMatchedTable(table, rawColumnReference.getColumnName());
            if (thisScopeTable == null) {
                throw new RuntimeException("can't find this scope table of AliasColumnReference");
            }

            if (thisScopeTable != table) {
                addSymbolReplace(reference, rawColumnReference.bindNewTable(thisScopeTable));
            }
        } else if (reference instanceof AliasColumnReference) {
            AliasColumnReference aliasColumnReference = (AliasColumnReference) reference;
            AliasTable<?> table = aliasColumnReference.getTable();
            AliasTable<?> thisScopeTable = innerTable.findMatchedTable(table, aliasColumnReference.getColumnAlias());
            if (thisScopeTable == null) {
                throw new RuntimeException("can't find this scope table of AliasColumnReference");
            }

            if (thisScopeTable != table) {
                addSymbolReplace(reference, aliasColumnReference.bindNewTable(thisScopeTable));
            }
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
