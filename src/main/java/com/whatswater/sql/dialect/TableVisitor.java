package com.whatswater.sql.dialect;


import com.whatswater.sql.table.*;

public interface TableVisitor {
    void visit(DbTable<?> table);
    void visit(SelectedTable table);
    void visit(JoinedTable table);
    void visit(ComplexTable table);


    static void visit(Table table, TableVisitor visitor) {
        if (table instanceof DbTable) {
            visitor.visit((DbTable<?>) table);
        } else if (table instanceof SelectedTable) {
            visitor.visit((SelectedTable) table);
        } else if (table instanceof JoinedTable) {
            visitor.visit((JoinedTable) table);
        } else if (table instanceof ComplexTable) {
            visitor.visit((ComplexTable) table);
        }
    }
}
