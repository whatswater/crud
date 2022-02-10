package com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation;


import io.vertx.ext.sql.assist.SqlAssist;
import java.util.Objects;

public class FlowInstanceTaskRelationQuery {
    
    Long prevTaskId;
    Long nextTaskId;
    
    
    public Long getPrevTaskId() {
        return this.prevTaskId;
    }
    
    public void setPrevTaskId(Long prevTaskId) {
        this.prevTaskId = prevTaskId;
    }
    
    public Long getNextTaskId() {
        return this.nextTaskId;
    }
    
    public void setNextTaskId(Long nextTaskId) {
        this.nextTaskId = nextTaskId;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(prevTaskId)) {
            sqlAssist.andEq(FlowInstanceTaskRelation.COLUMN_PREV_TASK_ID, prevTaskId);
        }
        if (Objects.nonNull(nextTaskId)) {
            sqlAssist.andEq(FlowInstanceTaskRelation.COLUMN_NEXT_TASK_ID, nextTaskId);
        }
        return sqlAssist;
    }
}
