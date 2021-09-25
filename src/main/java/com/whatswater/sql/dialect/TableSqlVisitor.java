package com.whatswater.sql.dialect;


import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinedTable;
import com.whatswater.sql.table.SelectedTable;

public class TableSqlVisitor implements TableVisitor {
    private StringBuilder sql;

    public TableSqlVisitor() {
        this.sql = new StringBuilder();
    }

    public TableSqlVisitor(StringBuilder sql) {
        this.sql = sql;
    }

    @Override
    public void visit(DbTable<?> table) {
        sql.append(table.getTableName());
        if (table.hasAlias()) {
            sql.append(" ").append(table.getAlias());
        }
    }

    @Override
    public void visit(SelectedTable table) {
        sql.append("(select");
        sql.append("from ");
        DbTable<?> dbTable = table.getRawTable();
        this.visit(dbTable);
        sql.append(")");
    }

    @Override
    public void visit(JoinedTable table) {
        table.getLeft();
    }

    @Override
    public void visit(ComplexTable table) {
    }

    public StringBuilder getSql() {
        return sql;
    }
}
