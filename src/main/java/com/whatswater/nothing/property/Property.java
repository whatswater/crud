package com.whatswater.nothing.property;


public class Property<T> {
    private String propertyName;
    private String dataType;
    private PropertyDbConfig dbConfig;
    private PropertyDictionaryConfig propertyDictionaryConfig;

    public static class PropertyDbConfig {
        private String columnName;
        private String jdbcType;
        private boolean primaryKey;
        private boolean notNull;
        private boolean unique;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getJdbcType() {
            return jdbcType;
        }

        public void setJdbcType(String jdbcType) {
            this.jdbcType = jdbcType;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }

        public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }

        public boolean isNotNull() {
            return notNull;
        }

        public void setNotNull(boolean notNull) {
            this.notNull = notNull;
        }

        public boolean isUnique() {
            return unique;
        }

        public void setUnique(boolean unique) {
            this.unique = unique;
        }
    }

    // 数据字典配置
    public static class PropertyDictionaryConfig {
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public PropertyDbConfig getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(PropertyDbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public PropertyDictionaryConfig getPropertyDictionaryConfig() {
        return propertyDictionaryConfig;
    }

    public void setPropertyDictionaryConfig(PropertyDictionaryConfig propertyDictionaryConfig) {
        this.propertyDictionaryConfig = propertyDictionaryConfig;
    }
}
