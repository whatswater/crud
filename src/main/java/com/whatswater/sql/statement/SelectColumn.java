package com.whatswater.sql.statement;


import com.whatswater.sql.alias.AliasPlaceholder;

public interface SelectColumn {
    default AliasPlaceholder getPlaceHolder() {
        return null;
    }
    String getAliasOrColumnName();
}
