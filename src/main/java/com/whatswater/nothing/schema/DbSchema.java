package com.whatswater.nothing.schema;


import com.whatswater.nothing.property.Properties;
import com.whatswater.nothing.property.Property;
import com.whatswater.nothing.schema.QueryParamNameParser.PropertyWithOperation;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.expression.reference.RawColumnReference;
import com.whatswater.sql.statement.SelectColumn;
import com.whatswater.sql.table.DbTable;
import com.whatswater.sql.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DbSchema implements Schema {
    private Properties properties;
    private String namespace;
    private String moduleName;
    private String schemaName;

    private DbTable<?> dbTable;
    private String tableName;
    private String remark;
    private String primaryKeyColumnName;

    public Properties getProperties() {
        return properties;
    }

    @Override
    public Table getBasicTable() {
        return dbTable;
    }

    @Override
    public BoolExpression getQueryCondition(String queryName, Object param) {
        PropertyWithOperation propertyWithOperation = QueryParamNameParser.parseName(properties, queryName);
        if (propertyWithOperation != null) {
            return propertyWithOperation.toBoolExpression(param);
        }
        return null;
    }

    @Override
    public Table toQueryParamTable(Map<String, Object> params) {
        BoolExpression where = null;
        for (Map.Entry<String, Object> entry: params.entrySet()) {
            BoolExpression c = getQueryCondition(entry.getKey(), entry.getValue());
            if (where == null) {
                where = c;
            } else if (c != null) {
                where = where.and(c);
            }
        }

        Table table = where != null ? dbTable.where(where) : dbTable;
        List<SelectColumn> selectColumnList = new ArrayList<>(properties.properties().size());
        for (Property<?> property: properties.properties()) {
            String columnName = property.getDbConfig().getColumnName();
            selectColumnList.add(new RawColumnReference(dbTable, columnName));
        }
        table.select(selectColumnList);
        return table;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public DbTable<?> getDbTable() {
        return dbTable;
    }

    public void setDbTable(DbTable<?> dbTable) {
        this.dbTable = dbTable;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPrimaryKeyColumnName() {
        return primaryKeyColumnName;
    }

    public void setPrimaryKeyColumnName(String primaryKeyColumnName) {
        this.primaryKeyColumnName = primaryKeyColumnName;
    }
}
