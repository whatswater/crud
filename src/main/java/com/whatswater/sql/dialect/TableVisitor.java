package com.whatswater.sql.dialect;


import com.whatswater.sql.table.*;

public interface TableVisitor {
    void visit(DbTable<?> table);
    void visit(SelectedTable table);
    void visit(JoinedTable table);
    void visit(ComplexTable table);

    default void visit(Table table) {
        if (table instanceof DbTable) {
            visit((DbTable<?>) table);
        } else if (table instanceof SelectedTable) {
            visit((SelectedTable) table);
        } else if (table instanceof JoinedTable) {
            visit((JoinedTable) table);
        } else if (table instanceof ComplexTable) {
            visit((ComplexTable) table);
        }
    }
}
