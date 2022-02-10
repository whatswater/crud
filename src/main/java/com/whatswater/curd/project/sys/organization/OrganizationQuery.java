package com.whatswater.curd.project.sys.organization;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class OrganizationQuery {
    private Long parentId;
    private String organizationCode;
    private String organizationName;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(organizationName)) {
            sqlAssist.andLike(Organization.COLUMN_ORGANIZATION_NAME, "%" + organizationName + "%");
        }
        if (parentId != null) {
            sqlAssist.andEq(Organization.COLUMN_PARENT_ID, parentId);
        }
        if (StrUtil.isNotEmpty(organizationCode)) {
            sqlAssist.andEq(Organization.COLUMN_ORGANIZATION_CODE, organizationCode);
        }
        return sqlAssist;
    }
}
