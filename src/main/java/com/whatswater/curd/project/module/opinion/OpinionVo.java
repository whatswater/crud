package com.whatswater.curd.project.module.opinion;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/12/21 8:04
 * @version:1.0
 */
public class OpinionVo extends Opinion {
    private String linkCode;
    private String linkName;

    public String getLinkCode() {
        return linkCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public static OpinionVo fromOpinion(Opinion opinion) {
        return BeanUtil.copyProperties(opinion, OpinionVo.class);
    }
}
