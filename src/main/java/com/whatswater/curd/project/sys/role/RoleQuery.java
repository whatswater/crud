package com.whatswater.curd.project.sys.role;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class RoleQuery {
    private String code;
    private String roleName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(code)) {
            sqlAssist.andEq(Role.COLUMN_CODE, code);
        }

        if (StrUtil.isNotEmpty(roleName)) {
            sqlAssist.andLike(Role.COLUMN_ROLE_NAME, "%" + roleName + "%");
        }
        return sqlAssist;
    }
}
