package com.whatswater.curd.project.module.workflow.flowInstanceLinkActor;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.util.Objects;

public class FlowInstanceLinkActorQuery {
    Long flowInstanceId;
    Long flowLinkId;
    String flowLinkCode;
    String actor;
    Long prevTaskId;


    public Long getFlowInstanceId() {
        return this.flowInstanceId;
    }

    public void setFlowInstanceId(Long flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }

    public Long getFlowLinkId() {
        return this.flowLinkId;
    }

    public void setFlowLinkId(Long flowLinkId) {
        this.flowLinkId = flowLinkId;
    }

    public String getFlowLinkCode() {
        return this.flowLinkCode;
    }

    public void setFlowLinkCode(String flowLinkCode) {
        this.flowLinkCode = flowLinkCode;
    }

    public String getActor() {
        return this.actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Long getPrevTaskId() {
        return this.prevTaskId;
    }

    public void setPrevTaskId(Long prevTaskId) {
        this.prevTaskId = prevTaskId;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowInstanceId)) {
            sqlAssist.andEq(FlowInstanceLinkActor.COLUMN_FLOW_INSTANCE_ID, flowInstanceId);
        }
        if (Objects.nonNull(flowLinkId)) {
            sqlAssist.andEq(FlowInstanceLinkActor.COLUMN_FLOW_LINK_ID, flowLinkId);
        }
        if (StrUtil.isNotEmpty(flowLinkCode)) {
            sqlAssist.andEq(FlowInstanceLinkActor.COLUMN_FLOW_LINK_CODE, flowLinkCode);
        }
        if (StrUtil.isNotEmpty(actor)) {
            sqlAssist.andEq(FlowInstanceLinkActor.COLUMN_ACTOR, actor);
        }
        if (Objects.nonNull(prevTaskId)) {
            sqlAssist.andEq(FlowInstanceLinkActor.COLUMN_PREV_TASK_ID, prevTaskId);
        }
        return sqlAssist;
    }
}
