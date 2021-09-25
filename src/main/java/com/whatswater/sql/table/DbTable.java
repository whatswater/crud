package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.alias.AliasPlaceholderGetter;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.mapper.ResultMapper;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.OrderByElement;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.annotation.TableName;
import com.whatswater.sql.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class DbTable<T> implements Table, TableCanRef, AliasPlaceholderGetter {
    private String tableName;
    private Class<T> entityClass;
    private AliasPlaceholder aliasPlaceholder;

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

    public Insert<T> toInsert() {
        return null;
    }

    @Override
    public <M> Query<M> toQuery(ResultMapper<M> mapper) {
        return null;
    }

    @Override
    public Table where(BoolExpression where) {
        SelectedTable selectedTable = new SelectedTable(this, aliasPlaceholder);
        selectedTable.setWhere(where);
        return selectedTable;
    }

    @Override
    public Table select(List<SelectColumn> selectList) {
        SelectedTable selectedTable = new SelectedTable(this, aliasPlaceholder);
        selectedTable.setSelectList(selectList);
        return selectedTable;
    }

    @Override
    public Table orderBy(List<OrderByElement> orderBy) {
        SelectedTable selectedTable = new SelectedTable(this, aliasPlaceholder);
        selectedTable.setOrderBy(orderBy);
        return selectedTable;
    }

    @Override
    public Grouped groupBy(List<Expression> groupBy) {
        return (having, selectList) -> {
            ComplexTable complexTable = new ComplexTable(this);
            complexTable.setSelectList(selectList);
            complexTable.setGroupBy(groupBy);
            complexTable.setHaving(having);

            return complexTable;
        };
    }

    @Override
    public Table newAlias(AliasPlaceholder aliasPlaceholder) {
        return new DbTable<T>(tableName, entityClass, aliasPlaceholder);
    }

    @Override
    public AliasPlaceholder getPlaceHolder() {
        return aliasPlaceholder;
    }

    @Override
    public boolean hasAlias() {
        return aliasPlaceholder != null && aliasPlaceholder.hasName();
    }

    public String getTableName() {
        return tableName;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getAlias() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return null;
    }

    @Override
    public String getAliasOrTableName() {
        if (hasAlias()) {
            return aliasPlaceholder.getAlias();
        }
        return tableName;
    }
}
