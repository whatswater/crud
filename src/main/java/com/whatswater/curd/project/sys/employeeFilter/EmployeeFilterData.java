package com.whatswater.curd.project.sys.employeeFilter;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

@Table("sys_employee_filter_data")
public class EmployeeFilterData {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_VALUE_TYPE = "value_type";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_CODE)
    String code;
    @TableColumn(COLUMN_PATH)
    String path;
    @TableColumn(COLUMN_VALUE)
    String value;
    @TableColumn(COLUMN_VALUE_TYPE)
    Integer valueType;

    public EmployeeFilterData() {

    }

    public EmployeeFilterData(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.code = json.getString(COLUMN_CODE);
        this.path = json.getString(COLUMN_PATH);
        this.value = json.getString(COLUMN_VALUE);
        this.valueType = json.getInteger(COLUMN_VALUE_TYPE);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public static SqlAssist codeSqlAssist(String code) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_CODE, code);

        return sqlAssist;
    }
}
