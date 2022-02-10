package com.whatswater.curd.project.sys.menu;


public class MenuName {
    private Long id;
    private String name;
    private String url;
    private Long parentId;
    private boolean leaf;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public static MenuName fromMenu(Menu menu) {
        MenuName menuName = new MenuName();
        menuName.setId(menu.getId());
        menuName.setName(menu.getName());
        menuName.setParentId(menu.getParentId());
        menuName.setLeaf(menu.isLeaf());
        return menuName;
    }
}
