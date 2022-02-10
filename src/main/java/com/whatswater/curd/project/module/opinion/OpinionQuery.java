package com.whatswater.curd.project.module.opinion;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class OpinionQuery {
    Long flowInstanceTaskId;
    String opinionApplicant;
    LocalDateTime createTimeStart;
    LocalDateTime createTimeEnd;


    public Long getFlowInstanceTaskId() {
        return this.flowInstanceTaskId;
    }

    public void setFlowInstanceTaskId(Long flowInstanceTaskId) {
        this.flowInstanceTaskId = flowInstanceTaskId;
    }

    public String getOpinionApplicant() {
        return this.opinionApplicant;
    }

    public void setOpinionApplicant(String opinionApplicant) {
        this.opinionApplicant = opinionApplicant;
    }

    public LocalDateTime getCreateTimeStart() {
        return this.createTimeStart;
    }

    public void setCreateTimeStart(LocalDateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public LocalDateTime getCreateTimeEnd() {
        return this.createTimeEnd;
    }

    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(flowInstanceTaskId)) {
            sqlAssist.andEq(Opinion.COLUMN_FLOW_INSTANCE_TASK_ID, flowInstanceTaskId);
        }
        if (StrUtil.isNotEmpty(opinionApplicant)) {
            sqlAssist.andEq(Opinion.COLUMN_OPINION_APPLICANT, opinionApplicant);
        }
        if (Objects.nonNull(createTimeStart)) {
            sqlAssist.andGte(Opinion.COLUMN_CREATE_TIME, createTimeStart);
        }
        if (Objects.nonNull(createTimeEnd)) {
            sqlAssist.andLte(Opinion.COLUMN_CREATE_TIME, createTimeEnd);
        }
        return sqlAssist;
    }
}
