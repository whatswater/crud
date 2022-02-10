package com.whatswater.curd.project.module.workflow.flowConstant;


import com.whatswater.curd.project.module.workflow.flowDefinition.FlowDefinition;

import java.util.List;

public interface FlowConstantConverter<T> {
    String namespace();
    T parse(FlowDefinition flowDefinition, List<FlowConstant> flowConstants);
}
