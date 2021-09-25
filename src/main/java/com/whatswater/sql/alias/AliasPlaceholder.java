package com.whatswater.sql.alias;


import com.whatswater.sql.utils.StringUtils;

public class AliasPlaceholder {
    private String alias;

    public AliasPlaceholder() {

    }

    public AliasPlaceholder(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean hasName() {
        return StringUtils.isNotEmpty(alias);
    }
}
