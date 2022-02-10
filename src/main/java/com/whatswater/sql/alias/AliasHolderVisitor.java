package com.whatswater.sql.alias;


public interface AliasHolderVisitor {
    void visitAliasHolder(AliasHolderVisitor.Handler handler);

    interface Handler {
        void handle(AliasPlaceholder aliasPlaceholder);
    }
}
