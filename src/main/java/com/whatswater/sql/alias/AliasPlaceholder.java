package com.whatswater.sql.alias;


import com.whatswater.sql.utils.StringUtils;

import java.util.UUID;

public class AliasPlaceholder {
    private String alias;
    private String uuid;

    public AliasPlaceholder() {
        this.uuid = UUID.randomUUID().toString();
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
