package com.whatswater.curd.project.module.workflow.flowLink;

// 不同类型环节的实现方法
// 不同类型环节的任务，可以存在不同的操作，比如：是否允许撤销；是否允许转办；是否需要抄送；竞办任务可以“确认办理”
// 不同类型环节的任务，任务的生成逻辑不同，参与者不同
// 不同类型环节的任务，设计器界面不同，需要维护的属性不同
// 不同类型环节的任务，可能会触发不同的系统事件

/**
 * <code>START</code> 开始节点
 * <code>END</code> 结束节点
 * <code>NORMAL</code> 正常节点
 * <code>CONCURRENT</code> 会签节点
 * <code>JOIN</code> 汇总节点（自动任务）
 * <code>LOCK</code> 竞办节点（自动取消）
 */
public enum FlowLinkType {
    START(1, "start"),
    END(2, "end"),
    NORMAL(3, "normal"),
    WAIT(4, "wait"),

    SWITCH(9, "switch"),
    BRANCH(10, "branch"),
    MERGE(11, "merge"),
    FORK(12, "fork"),
    JOIN(13, "join"),
    LOCK(14, "lock"),

    CONCURRENT(33, "concurrent"),
    ;

    private final Integer id;
    private final String code;

    FlowLinkType(Integer id, String code) {
        this.id = id;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public static FlowLinkType findByCode(String code) {
        for (FlowLinkType typeEnum: FlowLinkType.values()) {
            if(typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
