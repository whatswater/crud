package com.whatswater.curd.project.module.workflow.flowLinkConstant;


import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import io.vertx.core.Future;

import java.util.List;

// 如何设计一个context，作为全局context，可以惰性加载的
public interface FlowLinkConstantConverter<T> {
    String namespace();
    Future<T> parse(FlowLink flowLink, List<FlowLinkConstant> constantList);
}
