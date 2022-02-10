package com.whatswater.curd.project.sys.menu;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

public class MenuQuery {
    private Long parentId;
    private String name;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(name)) {
            sqlAssist.andLike(Menu.COLUMN_NAME, "%" + name + "%");
        }
        if (parentId != null) {
            sqlAssist.andEq(Menu.COLUMN_PARENT_ID, parentId);
        }
        return sqlAssist;
    }
}
