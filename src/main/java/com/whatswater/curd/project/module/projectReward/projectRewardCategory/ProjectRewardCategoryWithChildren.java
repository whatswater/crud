package com.whatswater.curd.project.module.projectReward.projectRewardCategory;


import cn.hutool.core.bean.BeanUtil;

import java.util.List;

public class ProjectRewardCategoryWithChildren extends ProjectRewardCategory {
    private List<ProjectRewardCategory> children;

    public List<ProjectRewardCategory> getChildren() {
        return children;
    }

    public void setChildren(List<ProjectRewardCategory> children) {
        this.children = children;
    }

    public static ProjectRewardCategoryWithChildren fromCategory(ProjectRewardCategory category, List<ProjectRewardCategory> children) {
        ProjectRewardCategoryWithChildren categoryWithChildren = BeanUtil.copyProperties(category, ProjectRewardCategoryWithChildren.class);
        categoryWithChildren.children = children;
        return categoryWithChildren;
    }
}
