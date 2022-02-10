package com.whatswater.curd.project.module.workflow.flowLink;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.module.workflow.flowLinkConstant.FlowLinkConstant;

import java.util.Map;
import java.util.Objects;

public class FlowLinkWithConstant extends FlowLink {
    Map<String, FlowLinkConstant> flowLinkConfig;
    Map<String, Tuple2<Boolean, Object>> flowLinkConfigParse;

    public Map<String, FlowLinkConstant> getFlowLinkConfig() {
        return flowLinkConfig;
    }

    public void setFlowLinkConfig(Map<String, FlowLinkConstant> flowLinkConfig) {
        this.flowLinkConfig = flowLinkConfig;
    }

    public String getConfigValue(String configName) {
        if (this.flowLinkConfig == null) {
            return StrUtil.EMPTY;
        }

        FlowLinkConstant constant = this.flowLinkConfig.get(configName);
        if (constant == null) {
            return StrUtil.EMPTY;
        }

        return constant.getConstantValue();
    }

    public static FlowLinkWithConstant from(FlowLink flowLink) {
        FlowLinkWithConstant flowLinkWithConstant = BeanUtil.copyProperties(flowLink, FlowLinkWithConstant.class);
        return flowLinkWithConstant;
    }

    public static FlowLinkWithConstant from(FlowLink flowLink, Map<String, FlowLinkConstant> flowLinkConfig) {
        FlowLinkWithConstant flowLinkWithConstant = BeanUtil.copyProperties(flowLink, FlowLinkWithConstant.class);
        flowLinkWithConstant.setFlowLinkConfig(flowLinkConfig);
        return flowLinkWithConstant;
    }
}
