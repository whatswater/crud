package com.whatswater.curd.project.module.opinion;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.sql.dialect.Dialect.SQL;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Table("b_opinion")
public class Opinion {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FLOW_INSTANCE_TASK_ID = "flow_instance_task_id";
    public static final String COLUMN_OPINION_APPLICANT = "opinion_applicant";
    public static final String COLUMN_OPINION_APPLICANT_NAME = "opinion_applicant_name";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_FLOW_INSTANCE_TASK_ID)
    Long flowInstanceTaskId;
    @TableColumn(COLUMN_OPINION_APPLICANT)
    String opinionApplicant;
    @TableColumn(COLUMN_OPINION_APPLICANT_NAME)
    String opinionApplicantName;
    @TableColumn(COLUMN_CONTENT)
    String content;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;

    public Opinion() {

    }

    public Opinion(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.flowInstanceTaskId = json.getLong(COLUMN_FLOW_INSTANCE_TASK_ID);
        this.opinionApplicant = json.getString(COLUMN_OPINION_APPLICANT);
        this.opinionApplicantName = json.getString(COLUMN_OPINION_APPLICANT_NAME);
        this.content = json.getString(COLUMN_CONTENT);
        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOpinionApplicantName() {
        return opinionApplicantName;
    }

    public void setOpinionApplicantName(String opinionApplicantName) {
        this.opinionApplicantName = opinionApplicantName;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist flowInstanceTaskIdListSqlAssist(List<Long> flowInstanceTaskIds) {
        SqlAssist sqlAssist = new SqlAssist();
        CrudUtils.andIn(COLUMN_FLOW_INSTANCE_TASK_ID, flowInstanceTaskIds);
        return sqlAssist;
    }
}
