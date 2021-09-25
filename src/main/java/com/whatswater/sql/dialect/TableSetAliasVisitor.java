package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.dialect.TableVisitor;
import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinedTable;
import com.whatswater.sql.table.SelectedTable;

public class TableSetAliasVisitor implements TableVisitor {
    private AliasFactory aliasFactory;

    public TableSetAliasVisitor(AliasFactory aliasFactory) {
        this.aliasFactory = aliasFactory;
    }

    @Override
    public void visit(DbTable<?> table) {
        if (!table.hasAlias()) {
            table.getPlaceHolder().setAlias(aliasFactory.getNextAlias());
        }
    }

    @Override
    public void visit(SelectedTable table) {
        if (!table.hasAlias()) {
            table.getPlaceHolder().setAlias(aliasFactory.getNextAlias());
        }
        visit(table.getRawTable());
    }

    @Override
    public void visit(JoinedTable table) {
        TableVisitor.visit(table.getLeft(), this);
        TableVisitor.visit(table.getRight(), this);
    }

    @Override
    public void visit(ComplexTable table) {
        if (!table.hasAlias()) {
            table.getPlaceHolder().setAlias(aliasFactory.getNextAlias());
        }
        TableVisitor.visit(table.getInnerTable(), this);
    }
}
