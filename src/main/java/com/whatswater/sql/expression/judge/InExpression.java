package com.whatswater.sql.expression.judge;


import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.JudgeOperatorExpression;
import com.whatswater.sql.expression.Literal;
import com.whatswater.sql.table.ComplexTable;
import com.whatswater.sql.table.SelectedTable;
import com.whatswater.sql.table.Table;

import java.util.Arrays;
import java.util.List;

public class InExpression implements JudgeOperatorExpression {
    private final Expression leftValue;
    private final ItemList inList;

    public InExpression(Expression leftValue, ItemList inList) {
        this.leftValue = leftValue;
        this.inList = inList;
    }

    @Override
    public void visitAliasHolder(Handler handler) {
        leftValue.visitAliasHolder(handler);
        inList.visitAliasHolder(handler);
    }

    public Expression getLeftValue() {
        return leftValue;
    }

    public ItemList getInList() {
        return inList;
    }

    public interface ItemList extends AliasHolderVisitor {
    }

    public static class LiteralList implements ItemList {
        List<Literal> literals;

        public LiteralList() {

        }

        public LiteralList(List<Literal> literals) {
            this.literals = literals;
        }

        public List<Literal> getLiterals() {
            return literals;
        }

        public void setLiterals(List<Literal> literals) {
            this.literals = literals;
        }

        @Override
        public void visitAliasHolder(Handler handler) {

        }
    }

    public static class SubSelect implements ItemList {
        Table table;

        public SubSelect(SelectedTable table) {
            this.table = table;
        }
        public SubSelect(ComplexTable table) {
            this.table = table;
        }

        @Override
        public void visitAliasHolder(Handler handler) {
            table.visitAliasHolder(handler);
        }
    }
}
