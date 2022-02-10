package com.whatswater.sql.statement;


import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.alias.AliasPlaceholder;

public interface SelectColumn extends AliasHolderVisitor {
    String getAliasOrColumnName();
    boolean matchColumnName(String columnName);
    boolean matchColumnName(AliasPlaceholder columnName);
}
