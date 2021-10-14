package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.annotation.TableName;
import com.whatswater.sql.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class DbTable<T> implements AliasTable<DbTable<T>> {
    private final String tableName;
    private final Class<T> entityClass;
    private final AliasPlaceholder aliasPlaceholder;

    public DbTable(Class<T> entityClass, String tableName) {
        this.tableName = tableName;
        this.entityClass = entityClass;
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public DbTable(String tableName, Class<T> entityClass, AliasPlaceholder aliasPlaceholder) {
        this.tableName = tableName;
        this.entityClass = entityClass;
        this.aliasPlaceholder = aliasPlaceholder;
    }

    public DbTable(Class<T> entityClass) {
        TableName tableName = entityClass.getDeclaredAnnotation(TableName.class);
        if (tableName == null || StringUtils.isEmpty(tableName.value())) {
            throw new RuntimeException("X1");
        }
        this.entityClass = entityClass;
        this.tableName = tableName.value();
        this.aliasPlaceholder = new AliasPlaceholder();
    }

    public Update toUpdate() {
        return new Update(this);
    }

    public Update toUpdate(List<UpdateColumn> valueSetList) {
        return new Update(this).setValueSetList(valueSetList);
    }

    public Update toUpdate(UpdateColumn... updateColumnArr) {
        return toUpdate(Arrays.asList(updateColumnArr));
    }

    public Delete toDelete() {
        return new Delete(this);
    }

    public Delete toDelete(BoolExpression where) {
        return new Delete(this, where);
    }

    public Insert<T> toInsert(T entity) {
        return new Insert<>(entity, this);
    }

    @Override
    public <M> Query<M> toQuery(ResultMapper<M> mapper) {
        return null;
    }

    @Override
    public Table where(BoolExpression where) {
        SelectedTable selectedTable = new SelectedTable(this);
        return selectedTable.where(where);
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        SelectedTable selectedTable = new SelectedTable(this);
        return selectedTable.select(selectList);
    }

    @Override
    public Table distinct(boolean distinct) {
        SelectedTable selectedTable = new SelectedTable(this);
        return selectedTable.distinct(distinct);
    }

    @Override
    public Table limit(Limit limit) {
        SelectedTable selectedTable = new SelectedTable(this);
        return selectedTable.limit(limit);
    }

    @Override
    public Table orderBy(List<OrderByElement> orderBy) {
        SelectedTable selectedTable = new SelectedTable(this, aliasPlaceholder);
        return selectedTable.orderBy(orderBy);
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> new ComplexTable(this).groupBy(groupBy).select(having, selectList);
    }

    @Override
    public DbTable<T> newAlias(AliasPlaceholder aliasPlaceholder) {
        return new DbTable<T>(tableName, entityClass, aliasPlaceholder);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    public boolean hasAlias() {
        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return tableName;
    }

    public boolean similar(DbTable<?> dbTable) {
        return dbTable.getTableName().equals(this.getTableName());
    }
}
