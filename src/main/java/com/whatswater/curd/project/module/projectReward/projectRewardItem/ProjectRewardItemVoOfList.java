package com.whatswater.curd.project.module.projectReward.projectRewardItem;


import cn.hutool.core.bean.BeanUtil;

public class ProjectRewardItemVoOfList extends ProjectRewardItem {
    private String category;
    private String rewardContent;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRewardContent() {
        return rewardContent;
    }

    public void setRewardContent(String rewardContent) {
        this.rewardContent = rewardContent;
    }

    public static ProjectRewardItemVoOfList fromProjectRewardItem(ProjectRewardItem projectRewardItem) {
        return BeanUtil.copyProperties(projectRewardItem, ProjectRewardItemVoOfList.class);
    }
}
