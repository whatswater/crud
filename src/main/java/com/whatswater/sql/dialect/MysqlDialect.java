package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.RawColumnRef;
import com.whatswater.sql.statement.Delete;
import com.whatswater.sql.statement.Insert;
import com.whatswater.sql.statement.Query;
import com.whatswater.sql.statement.Update;
import com.whatswater.sql.statement.Update.UpdateColumn;
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
            sql.append(sqlVisitor.getSql()).append(",");
            sqlVisitor.clearSql();
        }
        if (",".equals(sql.substring(sql.length() - 1, sql.length()))) {
            sql.deleteCharAt(sql.length() - 1);
        }
        sql.append(" where ");
        sqlVisitor.visit(update.getWhere());
        sql.append(sqlVisitor.getSql());
        sqlVisitor.clearSql();

        // 序列化where条件
        return new SqlAndParam(sql.toString(), sqlVisitor.getParams());
    }

    @Override
    public SqlAndParam toSql(Delete delete) {
        return null;
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
