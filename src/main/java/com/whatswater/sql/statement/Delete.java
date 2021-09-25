package com.whatswater.sql.statement;


import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.table.DbTable;

// 暂时只支持构建单表删除语句，不支持多表删除
public class Delete {
    private DbTable<?> dbTable;
    private BoolExpression where;

    public Delete(DbTable<?> dbTable) {
        this.dbTable = dbTable;
    }

    public Delete(DbTable<?> dbTable, BoolExpression where) {
        this.dbTable = dbTable;
        this.where = where;
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public BoolExpression getWhere() {
        return where;
    }
}
