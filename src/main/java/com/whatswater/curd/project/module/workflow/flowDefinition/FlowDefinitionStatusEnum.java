package com.whatswater.curd.project.module.workflow.flowDefinition;


import com.whatswater.curd.project.common.LoadPageData.DictItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FlowDefinitionStatusEnum {
    INIT(1, "初始"),
    DEPLOY(2, "部署"),
    DISABLED(3, "禁用"),
    DRAFT(4, "草稿"),
    ;

    private final Integer code;
    private final String name;

    FlowDefinitionStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static boolean canDeploy(int code) {
        return code == INIT.getCode();
    }

    public static FlowDefinitionStatusEnum getByCode(Integer code) {
        for (FlowDefinitionStatusEnum statusEnum: FlowDefinitionStatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static List<DictItem> toDictItemList() {
        return Arrays
            .stream(FlowDefinitionStatusEnum.values())
            .map(statusEnum -> DictItem.of(statusEnum.getCode().toString(), statusEnum.getName()))
            .collect(Collectors.toList());
    }
}
