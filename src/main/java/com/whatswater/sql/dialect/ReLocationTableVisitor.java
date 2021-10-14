package com.whatswater.sql.dialect;


import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinedTable;
import com.whatswater.sql.table.SelectedTable;

import java.util.List;

/**
 * @author whatswater
 */
public class ReLocationTableVisitor implements TableVisitor {
    private List<DbTable<?>> newDbTables;

    @Override
    public void visit(DbTable<?> table) {

    }

    @Override
    public void visit(SelectedTable table) {
        DbTable<?> dbTable = table.getRawTable();
        DbTable<?> similar = findSimilar(dbTable);
        if (similar == null || similar == dbTable) {
            return;
        }

        List<SelectColumn> selectColumnList = table.getSelectList();
        ReLocationSelectColumnVisitor selectColumnVisitor = new ReLocationSelectColumnVisitor(similar, selectColumnList.size());

        for (SelectColumn selectColumn: selectColumnList) {
            SelectColumnVisitor.visit(selectColumn, selectColumnVisitor);
        }
        // selectColumnVisitor;
    }

    @Override
    public void visit(JoinedTable table) {

    }

    @Override
    public void visit(ComplexTable table) {

    }

    private DbTable<?> findSimilar(DbTable<?> dbTable) {
        if (newDbTables == null) {
            return null;
        }

        for (DbTable<?> newDbTable: newDbTables) {
            if (newDbTable.similar(dbTable)) {
                return newDbTable;
            }
        }
        return null;
    }
}
