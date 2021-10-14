package com.whatswater.sql.statement;


import com.whatswater.sql.table.DbTable;

// TODO 添加一个TableInfo对象，作为缓存
public class Insert<T> {
    T entity;
    private DbTable<T> dbTable;
    private Class<T> entityClass;

    public Insert(T entity) {
        this.entity = entity;
    }

    public Insert(T entity, DbTable<T> dbTable) {
        this.entity = entity;
        this.dbTable = dbTable;
        this.entityClass = dbTable.getEntityClass();
    }

    public Insert<T> setTable(DbTable<T> dbTable) {
        this.dbTable = dbTable;
        this.entityClass = dbTable.getEntityClass();
        return this;
    }

    public T getEntity() {
        return entity;
    }

    public DbTable<T> getDbTable() {
        return dbTable;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
