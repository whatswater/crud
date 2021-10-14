package com.whatswater.sql.table;


import com.whatswater.sql.alias.AliasPlaceholder;

public interface AliasTable<T extends AliasTable<T>> extends Table {
    T newAlias(AliasPlaceholder aliasPlaceholder);
    default T newAlias() {
        return newAlias(new AliasPlaceholder());
    }

    String getAliasOrTableName();
    AliasPlaceholder getPlaceHolder();
}
