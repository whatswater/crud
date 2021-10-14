package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.*;

import java.util.List;

public class TableSetAliasVisitor implements TableVisitor {
    private AliasFactory aliasFactory;

    public TableSetAliasVisitor(AliasFactory aliasFactory) {
        this.aliasFactory = aliasFactory;
    }

    @Override
    public void visit(DbTable<?> table) {
        setAliasName(table, aliasFactory);
    }

    @Override
    public void visit(SelectedTable table) {
        setColumnAlias(table.getSelectList(), aliasFactory);
        visit(table.getRawTable());
    }

    @Override
    public void visit(JoinedTable table) {
        Table left = table.getLeft();
        if (left instanceof AliasTable) {
            setAliasName((AliasTable<?>) left, aliasFactory);
        }

        Table right = table.getRight();
        if (right instanceof AliasTable) {
            setAliasName((AliasTable<?>) right, aliasFactory);
        }

        TableVisitor.visit(table.getLeft(), this);
        TableVisitor.visit(table.getRight(), this);
    }

    @Override
    public void visit(ComplexTable table) {
        setColumnAlias(table.getSelectList(), aliasFactory);
        TableVisitor.visit(table.getInnerTable(), this);
    }

    public static void setColumnAlias(List<SelectColumn> columnList, AliasFactory aliasFactory) {
        for (SelectColumn column: columnList) {
            AliasPlaceholder placeholder = column.getPlaceHolder();
            if (placeholder != null && (!placeholder.hasName())) {
                placeholder.setAlias(aliasFactory.getNextAlias());
            }
        }
    }

    public static void setAliasName(AliasTable<?> aliasTable, AliasFactory aliasFactory) {
        AliasPlaceholder placeholder = aliasTable.getPlaceHolder();
        if (placeholder != null && (!placeholder.hasName())) {
            placeholder.setAlias(aliasFactory.getNextAlias());
        }
    }
}
