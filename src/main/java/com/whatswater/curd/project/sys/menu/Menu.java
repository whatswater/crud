package com.whatswater.curd.project.sys.menu;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.*;

import java.util.Arrays;

@Table("sys_menu")
public class Menu {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_LEAF = "leaf";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PERMISSION = "permission";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ICON = "icon";
    public static final String COLUMN_URL = "url";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_PATH)
    private String path;
    @TableColumn(COLUMN_LEVEL)
    private Integer level;
    @TableColumn(COLUMN_PARENT_ID)
    private Long parentId;
    @TableColumn(COLUMN_NAME)
    private String name;
    @TableColumn(COLUMN_PERMISSION)
    private String permission;
    @TableColumn(COLUMN_TYPE)
    private Integer type;
    @TableColumn(COLUMN_ICON)
    private String icon;
    @TableColumn(COLUMN_URL)
    private String url;
    @TableColumn(COLUMN_LEAF)
    private boolean leaf;

    public Menu() {

    }

    public Menu(JsonObject jsonObject) {
        this.id = jsonObject.getLong(COLUMN_ID);
        this.path = jsonObject.getString(COLUMN_PATH);
        this.level = jsonObject.getInteger(COLUMN_LEVEL);
        this.parentId = jsonObject.getLong(COLUMN_PARENT_ID);
        this.name = jsonObject.getString(COLUMN_NAME);
        this.permission = jsonObject.getString(COLUMN_PERMISSION);
        this.type =  jsonObject.getInteger(COLUMN_TYPE);
        this.icon = jsonObject.getString(COLUMN_ICON);
        this.url = jsonObject.getString(COLUMN_URL);
        Integer leaf = jsonObject.getInteger(COLUMN_LEAF);
        this.leaf = leaf != null && leaf.equals(1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String childPath() {
        String path = this.path;
        if (StrUtil.isEmpty(path)) {
            return CrudConst.PATH_SPLIT_CHAR + this.id;
        } else {
            if (!path.endsWith(CrudConst.PATH_SPLIT_CHAR)) {
                return path + CrudConst.PATH_SPLIT_CHAR + this.id;
            } else {
                return path + this.id;
            }
        }
    }

    public int childLevel() {
        return (level == null ? 0 : level) + 1;
    }

    public static int getLevel(String path) {
        if (StrUtil.isEmpty(path)) {
            return 0;
        }

        if (path.endsWith(CrudConst.PATH_SPLIT_CHAR)) {
            path = path.substring(0, path.length() - 1);
        }
        return path.split(CrudConst.PATH_SPLIT_CHAR).length;
    }

    public static SqlAssist menuSqlAssist(MenuTypeEnum... menuType) {
        Object[] value = new Object[menuType.length];
        for (int i = 0; i < value.length; i++) {
            value[i] = menuType[i].getId();
        }
        return CrudUtils.andIn(COLUMN_TYPE, Arrays.asList(value));
    }

    public static SqlAssist parentIdSqlAssist(long parentId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_PARENT_ID, parentId);
        return sqlAssist;
    }

    public static SqlAssist parentIdNameSqlAssist(long parentId, String name) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_PARENT_ID, parentId);
        sqlAssist.andLike(COLUMN_NAME, "%" + name + "%");

        sqlAssist.setRowSize(1);
        return sqlAssist;
    }

    public static SqlAssist urlSqlAssist(String url) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_URL, url);

        sqlAssist.setRowSize(1);
        return sqlAssist;
    }
}
