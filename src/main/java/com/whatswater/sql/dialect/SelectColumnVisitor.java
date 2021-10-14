package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.Alias;
import com.whatswater.sql.expression.reference.AliasColumnReference;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.SelectColumn;

public interface SelectColumnVisitor {
    void visit(AliasColumnReference aliasColumnReference);
    void visit(Alias alias);
    void visit(RawColumnReference rawColumnReference);

    static void visit(SelectColumn selectColumn, SelectColumnVisitor visitor) {
        if (selectColumn instanceof AliasColumnReference) {
            visitor.visit((AliasColumnReference) selectColumn);
        } else if (selectColumn instanceof Alias) {
            visitor.visit((Alias) selectColumn);
        } else if (selectColumn instanceof RawColumnReference) {
            visitor.visit((RawColumnReference) selectColumn);
        }
    }
}
