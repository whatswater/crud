package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.RawColumnRef;
import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.utils.StringUtils;


public class MysqlDialect implements Dialect {

    @Override
    public SqlAndParam toSql(Update update) {
        AliasFactory aliasFactory = new AliasFactory();
        Table table = update.getTable();
        setTableAlias(table, aliasFactory);

        StringBuilder sql = new StringBuilder("update");
        // 序列化table
        TableSqlVisitor tableVisitor = new TableSqlVisitor();
        TableVisitor.visit(table, tableVisitor);
        sql.append(" ").append(tableVisitor.getSql()).append(" set ");

        ExpressionSqlVisitor sqlVisitor = new ExpressionSqlVisitor();
        for (UpdateColumn valueSet: update.getValueSetList()) {
            RawColumnRef raw = valueSet.getColumn();
            String tableAlias = raw.getTable().getAlias();

            String prefix = "";
            if (StringUtils.isNotEmpty(tableAlias)) {
                prefix = tableAlias + ".";
            }

            sql.append(prefix).append(raw.getColumnName()).append(" = ");

            Expression expression = valueSet.getValue();
            sqlVisitor.visit(expression);
            sql.append(sqlVisitor.getAndClearSql()).append(",");
        }
        if (",".equals(sql.substring(sql.length() - 1, sql.length()))) {
            sql.deleteCharAt(sql.length() - 1);
        }
        if (update.getWhere() != null) {
            sql.append(" where ");
            sqlVisitor.visit(update.getWhere());
            sql.append(sqlVisitor.getAndClearSql());
        }
        if (update.getLimit() != null) {
            sql.append(" limit ").append(update.getLimit().getSize());
        }

        return new SqlAndParam(sql.toString(), sqlVisitor.getParams());
    }

    @Override
    public SqlAndParam toSql(Delete delete) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        DbTable<?> dbTable = delete.getDbTable();

        AliasFactory aliasFactory = new AliasFactory();
        setTableAlias(dbTable, aliasFactory);

        sql.append(dbTable.getTableName());
        if (dbTable.hasAlias()) {
            sql.append(" ").append(dbTable.getAlias());
        }
        if (delete.getWhere() != null) {
            sql.append(" where ");
            ExpressionSqlVisitor sqlVisitor = new ExpressionSqlVisitor();
            sqlVisitor.visit(delete.getWhere());
            sql.append(sqlVisitor.getAndClearSql());

            return new SqlAndParam(sql.toString(), sqlVisitor.getParams());
        }

        return new SqlAndParam(sql.toString());
    }

    @Override
    public SqlAndParam toSql(Query<?> query) {
        return null;
    }

    @Override
    public SqlAndParam toSql(Insert<?> insert) {
        return null;
    }


    public static void setTableAlias(Table table, AliasFactory aliasFactory) {
        TableVisitor.visit(table, new TableSetAliasVisitor(aliasFactory));
    }
}
