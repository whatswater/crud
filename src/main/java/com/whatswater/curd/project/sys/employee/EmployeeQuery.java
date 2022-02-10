package com.whatswater.curd.project.sys.employee;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class EmployeeQuery {
    private String phone;
    private Integer status;
    private String loginName;
    private String name;
    private Long organizationId;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(loginName)) {
            sqlAssist.andEq(Employee.COLUMN_LOGIN_NAME, loginName);
        }
        if (StrUtil.isNotEmpty(name)) {
            sqlAssist.andLike(Employee.COLUMN_NAME, "%" + name + "%");
        }
        if (organizationId != null) {
            sqlAssist.andEq(Employee.COLUMN_ORGANIZATION_ID, organizationId);
        }
        if (status != null) {
            sqlAssist.andEq(Employee.COLUMN_STATUS, status);
        }
        if (StrUtil.isNotEmpty(phone)) {
            sqlAssist.andLike(Employee.COLUMN_PHONE, "%" + phone + "%");
        }
        return sqlAssist;
    }
}
