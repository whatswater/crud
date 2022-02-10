package com.whatswater.curd.template;


import java.util.List;

public class BaseModel {
    private Long id;
    private String catalog;
    private String moduleName;
    private String tableName;
    private String entityName;
    private String primaryKey;
    private PrimaryKeyGeneratorTypeEnum primaryKeyGeneratorType;
    private String remark;

    private List<BaseModelProperty> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public PrimaryKeyGeneratorTypeEnum getPrimaryKeyGeneratorType() {
        return primaryKeyGeneratorType;
    }

    public void setPrimaryKeyGeneratorType(PrimaryKeyGeneratorTypeEnum primaryKeyGeneratorType) {
        this.primaryKeyGeneratorType = primaryKeyGeneratorType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<BaseModelProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<BaseModelProperty> properties) {
        this.properties = properties;
    }
}
