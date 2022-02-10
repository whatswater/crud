package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.reference.RawColumnReference;

// Table可以分为两类
// 1、可以有单独别名的
// 2、可以update或者delete
// 目前只区分了AliasTable。是否能够update
public interface AliasTable<T extends AliasTable<T>> extends Table {
    T newAlias(AliasPlaceholder aliasPlaceholder);
    default T newAlias() {
        return newAlias(new AliasPlaceholder());
    }

    boolean hasAlias();
    String getAliasOrTableName();
    AliasPlaceholder getPlaceHolder();

    default RawColumnReference columnReference(String columnName) {
        return new RawColumnReference(this, columnName);
    }
}
