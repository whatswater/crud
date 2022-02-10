package com.whatswater.sql.statement;


import com.whatswater.sql.dialect.ReBindReferenceExpressionVisitor;
import com.whatswater.sql.dialect.ReBindTableSelectColumnVisitor;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.expression.literal.JdbcParameter;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.JoinType;
import com.whatswater.sql.table.JoinedTable;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Update {
    private List<UpdateColumn> valueSetList;
    private JoinedTable table;
    private final DbTable<?> dbTable;
    private BoolExpression where;
    private Limit limit;
    private Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap;

    public Update(DbTable<?> dbTable) {
        this.dbTable = dbTable;
    }

    public Update set(RawColumnReference sqlColumn, Expression value) {
        if (!dbTable.isSimilar(sqlColumn.getTable())) {
            throw new RuntimeException("X3");
        }

        UpdateColumn statement = new UpdateColumn(sqlColumn, value);
        if (valueSetList == null) {
            valueSetList = new ArrayList<>();
        }
        valueSetList.add(statement);
        return this;
    }

    public Update set(RawColumnReference sqlColumn, Object value) {
        return set(sqlColumn, new JdbcParameter(value));
    }

    public Update setValueSetList(List<UpdateColumn> valueSetList) {
        for (UpdateColumn statement: valueSetList) {
            if (!dbTable.equals(statement.getColumn().getTable())) {
                throw new RuntimeException("X3");
            }
        }
        this.valueSetList = valueSetList;
        return this;
    }

    public Update join(Table right, BoolExpression boolExpression) {
        if (this.table == null) {
            this.table = this.dbTable.join(right, boolExpression, JoinType.inner);
        } else {
            this.table = this.table.join(right, boolExpression, JoinType.inner);
        }
        return this;
    }
    public Update leftJoin(Table right, BoolExpression boolExpression) {
        if (this.table == null) {
            this.table = this.dbTable.join(right, boolExpression, JoinType.left);
        } else {
            this.table = this.table.join(right, boolExpression, JoinType.left);
        }
        return this;
    }

    public Update where(BoolExpression where) {
        this.where = where;
        return this;
    }

    public Update limit(Limit limit) {
        this.limit = limit;
        return this;
    }

    public Update limit(int value) {
        return limit(new Limit(value));
    }

    public Limit getLimit() {
        return limit;
    }

    public Map<ReferenceExpression, ReferenceExpression> reBindColumnReference() {
        Table realTable = getRealTable();
        ReBindReferenceExpressionVisitor visitor = new ReBindReferenceExpressionVisitor(realTable);
        for (UpdateColumn updateColumn: valueSetList) {
            RawColumnReference reference = updateColumn.getColumn();
            visitor.visit(reference);

            Expression expression = updateColumn.getValue();
            visitor.visit(expression);
        }
        if (where != null) {
            visitor.visit(where);
        }
        if (realTable instanceof JoinedTable) {
            JoinedTable joinedTable = (JoinedTable) realTable;
            if (joinedTable.getJoinCondition() != null) {
                visitor.visit(joinedTable.getJoinCondition());
            }
        }
        mergeSymbolReplaceMap(visitor.getSymbolReplaceMap());
        return symbolReplaceMap;
    }


    public Table getRealTable() {
        if (this.table != null) {
            return table;
        }
        return this.dbTable;
    }

    public static class UpdateColumn {
        private RawColumnReference column;
        private Expression value;

        public UpdateColumn(RawColumnReference column, Expression value) {
            this.column = column;
            this.value = value;
        }

        public RawColumnReference getColumn() {
            return column;
        }

        public Expression getValue() {
            return value;
        }
    }

    public List<UpdateColumn> getValueSetList() {
        return valueSetList;
    }

    public Table getTable() {
        return table;
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public BoolExpression getWhere() {
        return where;
    }

    private void mergeSymbolReplaceMap(Map<ReferenceExpression, ReferenceExpression> from) {
        if (from == null) {
            return;
        }

        if (symbolReplaceMap == null) {
            this.symbolReplaceMap = from;
        }
        this.symbolReplaceMap.putAll(from);
    }

    private void addSymbolReplace(ReferenceExpression oldExpression, ReferenceExpression newExpression) {
        if (symbolReplaceMap == null) {
            symbolReplaceMap = new HashMap<>();
        }

        symbolReplaceMap.put(oldExpression, newExpression);
    }
}
