package com.whatswater.sql.dialect;


import com.whatswater.sql.alias.AliasFactory;
import com.whatswater.sql.expression.Expression;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.*;
import com.whatswater.sql.statement.Update.UpdateColumn;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;
import com.whatswater.sql.table.annotation.*;
import com.whatswater.sql.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO
//  1、aliasFactory需要处理名称重复的情况，不能与其他列的名称重复
//  2、Table做成可变对象，仅仅在生成新table或者设置alias时创建新对象（已完成）
//  3、当一个表作为内部表时，必须指定别名，若没有则报错（已完成）
//  4、当一个表是内部表时，其列被系统自动转化
//  5、提供addSelect(ColumnRef, AliasPlaceHolder)方法
//  6、提供各种visitor接口，保证实现的正确性
public class MysqlDialect implements Dialect {

    @Override
    public SQL toSql(Update update) {
        AliasFactory aliasFactory = new AliasFactory();
        Table table = update.getTable();
        setTableAlias(table, aliasFactory);

        // 序列化table
        StringBuilder sql = new StringBuilder("update");
        ExpressionSqlVisitor expressionSqlVisitor = new ExpressionSqlVisitor();
        TableToSqlVisitor tableVisitor = new TableToSqlVisitor(expressionSqlVisitor);
        TableVisitor.visit(table, tableVisitor);
        sql.append(" ").append(tableVisitor.getSql()).append(" set ");

        ExpressionSqlVisitor sqlVisitor = new ExpressionSqlVisitor();
        for (UpdateColumn valueSet: update.getValueSetList()) {
            RawColumnReference raw = valueSet.getColumn();
            String tableAlias = raw.getTable().getPlaceHolder().getAlias();

            String prefix = "";
            if (StringUtils.isNotEmpty(tableAlias)) {
                prefix = tableAlias + ".";
            }

            sql.append(prefix).append(raw.getAliasOrColumnName()).append(" = ");

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

        return new SQL(sql.toString(), sqlVisitor.getParams());
    }

    @Override
    public SQL toSql(Delete delete) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        DbTable<?> dbTable = delete.getDbTable();

        AliasFactory aliasFactory = new AliasFactory();
        setTableAlias(dbTable, aliasFactory);

        sql.append(dbTable.getTableName());
        if (dbTable.hasAlias()) {
            sql.append(" ").append(dbTable.getPlaceHolder().getAlias());
        }
        if (delete.getWhere() != null) {
            sql.append(" where ");
            ExpressionSqlVisitor sqlVisitor = new ExpressionSqlVisitor();
            sqlVisitor.visit(delete.getWhere());
            sql.append(sqlVisitor.getAndClearSql());

            return new SQL(sql.toString(), sqlVisitor.getParams());
        }

        return new SQL(sql.toString());
    }

    @Override
    public SQL toSql(Query<?> query) {
        AliasFactory aliasFactory = new AliasFactory();
        Table table = query.getTable();
        setTableAlias(table, aliasFactory);

        ExpressionSqlVisitor expressionSqlVisitor = new ExpressionSqlVisitor();
        TableToSqlVisitor tableVisitor = new TableToSqlVisitor(expressionSqlVisitor);
        TableVisitor.visit(table, tableVisitor);

        return new SQL(tableVisitor.getSql().toString(), tableVisitor.getParams());
    }

    @Override
    public SQL toSql(Insert<?> insert) {
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(insert.getDbTable().getTableName()).append("(");

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

            sql.append(tableFieldInfo.getColumn()).append(", ");
            params.add(value);
        }
        if (params.isEmpty()) {
            throw new RuntimeException("X6");
        }
        sql.replace(sql.length() - 2, sql.length(), ") values (");
        for (Object ignored : params) {
            sql.append("?, ");
        }
        sql.replace(sql.length() - 2, sql.length(), ")");
        return new SQL(sql.toString(), params);
    }

    public static void setTableAlias(Table table, AliasFactory aliasFactory) {
        TableVisitor.visit(table, new TableSetAliasVisitor(aliasFactory));
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
