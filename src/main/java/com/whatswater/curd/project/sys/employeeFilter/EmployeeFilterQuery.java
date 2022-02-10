package com.whatswater.curd.project.sys.employeeFilter;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class EmployeeFilterQuery {
    String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(code)) {
            sqlAssist.andEq(EmployeeFilter.COLUMN_CODE, code);
        }
        return sqlAssist;
    }
}
