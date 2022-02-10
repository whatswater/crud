package com.whatswater.curd.project.sys.employee;


import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.util.List;

@Table("sys_employee")
public class Employee {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LOGIN_NAME = "login_name";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_INIT_PASSWORD = "init_password";
    public static final String COLUMN_ORGANIZATION_ID = "organization_id";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_UPDATE_TRACE_ID = "update_trace_id";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_NAME)
    private String name;
    @TableColumn(COLUMN_LOGIN_NAME)
    private String loginName;
    @TableColumn(COLUMN_PASSWORD)
    private String password;
    @TableColumn(COLUMN_INIT_PASSWORD)
    private String initPassword;
    @TableColumn(COLUMN_ORGANIZATION_ID)
    private Long organizationId;

    @TableColumn(COLUMN_PHONE)
    private String phone;
    @TableColumn(COLUMN_EMAIL)
    private String email;
    @TableColumn(COLUMN_STATUS)
    private Integer status;
    @TableColumn(COLUMN_UPDATE_TRACE_ID)
    private long updateTraceId;

    public Employee() {

    }

    public Employee(JsonObject jsonObject) {
        this.id = jsonObject.getLong(COLUMN_ID);
        this.name = jsonObject.getString(COLUMN_NAME);
        this.loginName = jsonObject.getString(COLUMN_LOGIN_NAME);
        this.password = jsonObject.getString(COLUMN_PASSWORD);
        this.initPassword = jsonObject.getString(COLUMN_PASSWORD);
        this.phone = jsonObject.getString(COLUMN_PHONE);
        this.email = jsonObject.getString(COLUMN_EMAIL);
        this.organizationId = jsonObject.getLong(COLUMN_ORGANIZATION_ID);
        this.status = jsonObject.getInteger(COLUMN_STATUS);
        this.updateTraceId = jsonObject.getLong(COLUMN_UPDATE_TRACE_ID);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInitPassword() {
        return initPassword;
    }

    public void setInitPassword(String initPassword) {
        this.initPassword = initPassword;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUpdateTraceId() {
        return updateTraceId;
    }

    public void setUpdateTraceId(long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SqlAssist loginNameSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_LOGIN_NAME, loginName);
        sqlAssist.setRowSize(1);
        return sqlAssist;
    }

    public static SqlAssist idListSqlAssist(List<Long> idList) {
        return CrudUtils.andIn(COLUMN_ID, idList);
    }

    public static SqlAssist loginNameListSqlAssist(List<String> loginNameList) {
        return CrudUtils.andIn(COLUMN_LOGIN_NAME, loginNameList);
    }

    public static SqlAssist organizationIdListSqlAssist(List<Long> orgIds) {
        return CrudUtils.andIn(COLUMN_ORGANIZATION_ID, orgIds);
    }
}
