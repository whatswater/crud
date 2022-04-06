package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.alias.AliasHolderVisitor;
import com.whatswater.sql.alias.AliasPlaceholder;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.LogicExpression;
import com.whatswater.sql.expression.ReferenceExpression;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.table.annotation.*;
import com.whatswater.sql.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class MysqlDialect implements Dialect {

    @Override
    public SQL toSql(Update update) {
        Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap = update.reBindColumnReference();
        Table table = update.getTable() == null ? update.getDbTable() : update.getTable();
        setAlias(table);

        SQL updateSql = new SQL();
        updateSql.append("update");

        ExpressionSqlVisitor tableSqlVisitor = new ExpressionSqlVisitor(new SQL());
        tableSqlVisitor.setSymbolReplaceMap(symbolReplaceMap);
        TableSqlVisitor tableVisitor = new TableSqlVisitor(tableSqlVisitor);
        tableVisitor.visit(table);
        updateSql.append(" ").append(tableVisitor.getSql()).append(" set ");

        for (UpdateColumn valueForSet: update.getValueSetList()) {
            updateSql
                .append(Expression.toSQL(tableSqlVisitor, valueForSet.getColumn()))
                .append(" = ")
                .append(Expression.toSQL(tableSqlVisitor, valueForSet.getValue()))
                .append(StringUtils.COMMA);
        }
        updateSql.deleteLastChar(StringUtils.COMMA);
        if (update.getWhere() != null) {
            updateSql.append(" where ").append(Expression.toSQL(tableSqlVisitor, update.getWhere()));
        }
        if (update.getLimit() != null) {
            updateSql.append(" limit ").append(update.getLimit().getSize());
        }

        return updateSql;
    }

    @Override
    public SQL toSql(Delete delete) {
        SQL deleteSql = new SQL();
        deleteSql.append("delete from ");
        DbTable<?> dbTable = delete.getDbTable();
        setAlias(dbTable);
        TableSqlVisitor tableVisitor = new TableSqlVisitor(new ExpressionSqlVisitor(new SQL()));
        tableVisitor.visit(dbTable);
        deleteSql.append(tableVisitor.getSql());

        if (delete.getWhere() != null) {
            Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap = delete.reBindColumnReference();
            BoolExpression where = delete.getWhere();
            if (where instanceof LogicExpression) {
                where = ((LogicExpression) where).flatten();
            }

            ExpressionSqlVisitor expressionSqlVisitor = new ExpressionSqlVisitor(new SQL());
            expressionSqlVisitor.setSymbolReplaceMap(symbolReplaceMap);
            expressionSqlVisitor.visit(where);
            deleteSql.append(" where ").append(expressionSqlVisitor.getSql().removeBrackets());
        }
        return deleteSql;
    }

    @Override
    public SQL toSql(Table table) {
        Map<ReferenceExpression, ReferenceExpression> symbolReplaceMap = table.reBindColumnReference();
        ExpressionSqlVisitor expressionSqlVisitor = new ExpressionSqlVisitor(new SQL());
        expressionSqlVisitor.setSymbolReplaceMap(symbolReplaceMap);

        setAlias(table);
        TableSqlVisitor tableVisitor = new TableSqlVisitor(expressionSqlVisitor);
        tableVisitor.visit(table);
        if (table.isSqlQuery()) {
            return tableVisitor.getSql();
        } else {
            SQL sql = new SQL(new StringBuilder("select * from "));
            return sql.append(tableVisitor.getSql());
        }
    }

    @Override
    public SQL toSql(Insert<?> insert) {
        SQL insertSql = new SQL();
        insertSql.append("insert into ").append(insert.getDbTable().getTableName()).append("(");

        Class<?> entityClass = insert.getEntityClass();
        TableInfo tableInfo = getTableInfo(entityClass);

        List<Object> params = new ArrayList<>();
        for (TableFieldInfo tableFieldInfo: tableInfo) {
            if (FieldStrategy.NEVER.equals(tableFieldInfo.getInsertStrategy())) {
                continue;
            }
            Field field = tableFieldInfo.getField();
            Object value = getFieldValue(field, insert.getEntity());

            TableId tableId = tableFieldInfo.getTableId();
            if (tableId != null && value == null) {
                IdType type = tableId.type();
                if (IdType.NONE.equals(type)) {
                    throw new RuntimeException("X7");
                }
                if (IdType.AUTO.equals(type)) {
                    continue;
                }
                if (IdType.UUID.equals(type)) {
                    value = uuid();
                }
            }
            else if (value == null && FieldStrategy.NOT_NULL.equals(tableFieldInfo.getInsertStrategy())) {
                continue;
            }

            insertSql.append(tableFieldInfo.getColumn()).append(StringUtils.COMMA);
            params.add(value);
        }
        if (params.isEmpty()) {
            throw new RuntimeException("Insert's params is empty when generate sql");
        }
        insertSql.deleteLastChar(StringUtils.COMMA).append(") values (");
        for (Object ignored : params) {
            insertSql.append("?").append(StringUtils.COMMA);
        }
        insertSql.deleteLastChar(StringUtils.COMMA).append(")");
        insertSql.addParam(params);
        return insertSql;
    }

    private AliasFactory setAlias(AliasHolderVisitor visitor) {
        Set<AliasPlaceholder> aliasPlaceholders = new HashSet<>();
        visitor.visitAliasHolder(aliasPlaceholders::add);

        AliasFactory aliasFactory = new AliasFactory();
        for (AliasPlaceholder aliasPlaceholder: aliasPlaceholders) {
            String alias = aliasPlaceholder.getAlias();
            if (StringUtils.isNotEmpty(alias)) {
                aliasFactory.addNamedAlias(alias);
            }
        }
        for (AliasPlaceholder aliasPlaceholder: aliasPlaceholders) {
            String alias = aliasPlaceholder.getAlias();
            if (StringUtils.isEmpty(alias)) {
                aliasPlaceholder.setAlias(aliasFactory.getNextAlias());
            }
        }
        return aliasFactory;
    }

    public TableInfo getTableInfo(Class<?> entityClass) {
        return TABLE_INFO_CACHE.computeIfAbsent(entityClass, MysqlDialect::resolveTableInfo);
    }

    private static TableInfo resolveTableInfo(Class<?> entityClass) {
        Field[] fields = entityClass.getDeclaredFields();

        List<TableFieldInfo> tableFieldInfoList = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            FieldStrategy insertStrategy = FieldStrategy.NOT_NULL;
            String columnName;

            TableField tableField = field.getDeclaredAnnotation(TableField.class);
            if (tableField != null) {
                if (!tableField.exist()) {
                    continue;
                }
                insertStrategy = tableField.insertStrategy();
                columnName = tableField.value();
                columnName = StringUtils.isEmpty(columnName) ? underline(field.getName()) : columnName;
            } else {
                columnName = underline(field.getName());
            }

            String propertyName = field.getName();
            TableId tableId = field.getDeclaredAnnotation(TableId.class);
            tableFieldInfoList.add(new TableFieldInfo(field, columnName, propertyName, insertStrategy, tableId));
        }
        return new TableInfo(entityClass, tableFieldInfoList);
    }

    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();
    public static class TableInfo implements Iterable<TableFieldInfo> {
        private final Class<?> entityType;
        private final List<TableFieldInfo> fieldInfoList;

        public TableInfo(Class<?> entityType, List<TableFieldInfo> fieldInfoList) {
            this.entityType = entityType;
            this.fieldInfoList = fieldInfoList;
        }

        public Class<?> getEntityType() {
            return entityType;
        }

        public List<TableFieldInfo> getFieldInfoList() {
            return fieldInfoList;
        }

        @Override
        public Iterator<TableFieldInfo> iterator() {
            return fieldInfoList.iterator();
        }
    }

    public static class TableFieldInfo {
        private final Field field;
        private final String column;
        private final String property;
        private final FieldStrategy insertStrategy;
        private final TableId tableId;

        public TableFieldInfo(Field field, String column, String property, FieldStrategy insertStrategy, TableId tableId) {
            this.field = field;
            this.column = column;
            this.property = property;
            this.insertStrategy = insertStrategy;
            this.tableId = tableId;
        }

        public Field getField() {
            return field;
        }

        public String getColumn() {
            return column;
        }

        public String getProperty() {
            return property;
        }

        public FieldStrategy getInsertStrategy() {
            return insertStrategy;
        }

        public TableId getTableId() {
            return tableId;
        }
    }

    private static String underline(String name) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                char lch = (char) (ch^32);
                if (i > 0) {
                    buf.append('_');
                }
                buf.append(lch);
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    public static Object getFieldValue(Field field, Object entity) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("X4");
        }
    }

    private static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
