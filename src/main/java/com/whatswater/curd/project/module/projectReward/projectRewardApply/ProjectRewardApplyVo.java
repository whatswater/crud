package com.whatswater.curd.project.module.projectReward.projectRewardApply;


import cn.hutool.core.bean.BeanUtil;
import com.whatswater.curd.project.sys.attachment.Attachment;

import java.util.List;

public class ProjectRewardApplyVo extends ProjectRewardApply {
    private List<Attachment> attachmentList;

    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public static ProjectRewardApplyVo fromProjectRewardApply(ProjectRewardApply apply) {
        return BeanUtil.copyProperties(apply, ProjectRewardApplyVo.class);
    }
}
