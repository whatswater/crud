package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.AliasTable;
import com.whatswater.sql.table.DbTable;

import java.util.ArrayList;
import java.util.List;

public class ReLocationSelectColumnVisitor implements SelectColumnVisitor {
    private final DbTable<?> newTable;
    List<SelectColumn> selectColumnList;

    public ReLocationSelectColumnVisitor(DbTable<?> newTable, int size) {
        this.newTable = newTable;
        this.selectColumnList = new ArrayList<>(size);
    }

    @Override
    public void visit(AliasColumnReference aliasColumnReference) {
        AliasTable<?> table = aliasColumnReference.getTable();
        if (!(table instanceof DbTable)) {
            selectColumnList.add(aliasColumnReference);
            return;
        }

        DbTable<?> dbTable = (DbTable<?>) table;
        if (dbTable != newTable && dbTable.similar(newTable)) {
            selectColumnList.add(aliasColumnReference.bindNewTable(newTable));
        } else {
            selectColumnList.add(aliasColumnReference);
        }
    }

    @Override
    public void visit(Alias alias) {
        // 想办法找到所有的RawColumnReference和DbTable
        selectColumnList.add(alias);

        Expression expression = alias.getExpression();

    }

    @Override
    public void visit(RawColumnReference rawColumnReference) {
        DbTable<?> dbTable = rawColumnReference.getTable();
        if (dbTable != newTable && dbTable.similar(newTable)) {
            selectColumnList.add(rawColumnReference.bindNewTable(newTable));
        } else {
            selectColumnList.add(rawColumnReference);
        }
    }
}
