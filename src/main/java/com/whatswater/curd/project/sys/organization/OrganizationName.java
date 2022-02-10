package com.whatswater.curd.project.sys.organization;


public class OrganizationName {
    private Long id;
    private String organizationCode;
    private String organizationName;
    private Long parentId;
    private boolean leaf;

    public OrganizationName() {
    }

    public OrganizationName(Long id, String organizationCode, String organizationName, boolean leaf) {
        this.id = id;
        this.organizationCode = organizationCode;
        this.organizationName = organizationName;
        this.leaf = leaf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public static OrganizationName fromOrganization(Organization organization) {
        OrganizationName ret = new OrganizationName(organization.getId(), organization.getOrganizationCode(), organization.getOrganizationName(), organization.isLeaf());
        ret.setParentId(organization.getParentId());
        return ret;
    }
}
