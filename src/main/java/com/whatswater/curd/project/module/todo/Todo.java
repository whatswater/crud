package com.whatswater.curd.project.module.todo;

import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.time.LocalDateTime;

@Table("b_todo")
public class Todo {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MODULE_NAME = "module_name";
    public static final String COLUMN_BUSINESS_TYPE = "business_type";
    public static final String COLUMN_PREV_EMPLOYEE = "prev_employee";
    public static final String COLUMN_PREV_LINK = "prev_link";
    public static final String COLUMN_PREV_EMPLOYEE_NAME = "prev_employee_name";
    public static final String COLUMN_ACTOR_EMPLOYEE = "actor_employee";
    public static final String COLUMN_ACTOR_EMPLOYEE_NAME = "actor_employee_name";
    public static final String COLUMN_BUSINESS_ID = "business_id";
    public static final String COLUMN_EXTRA_INFO = "extra_info";
    public static final String COLUMN_TASK_ID = "task_id";
    public static final String COLUMN_UPDATE_TRACE_ID = "update_trace_id";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_TITLE)
    private String title;
    @TableColumn(COLUMN_CREATE_TIME)
    private LocalDateTime createTime;

    // 前一个环节
    @TableColumn(COLUMN_PREV_LINK)
    private String prevLink;
    // 前一个环节的经手人
    @TableColumn(COLUMN_PREV_EMPLOYEE)
    private String prevEmployee;
    // 前一个环节的姓名
    @TableColumn(COLUMN_PREV_EMPLOYEE_NAME)
    private String prevEmployeeName;

    @TableColumn(COLUMN_ACTOR_EMPLOYEE)
    private String actorEmployee;
    @TableColumn(COLUMN_ACTOR_EMPLOYEE_NAME)
    private String actorEmployeeName;

    // 待办对应的功能模块名称
    @TableColumn(COLUMN_MODULE_NAME)
    private String moduleName;
    // 业务类型
    @TableColumn(COLUMN_BUSINESS_TYPE)
    private String businessType;
    @TableColumn(COLUMN_BUSINESS_ID)
    private String businessId;
    // 关联的业务数据（包含动作）
    @TableColumn(COLUMN_EXTRA_INFO)
    private String extraInfo;
    // 关联的任务Id
    @TableColumn(COLUMN_TASK_ID)
    private Long taskId;

    // 当前的状态（已读、未读、已完成）
    @TableColumn(COLUMN_STATUS)
    private Integer status;
    @TableColumn(COLUMN_UPDATE_TRACE_ID)
    private Long updateTraceId;

    public Todo() {
    }

    public Todo(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.title = json.getString(COLUMN_TITLE);
        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
        this.status = json.getInteger(COLUMN_STATUS);
        this.moduleName = json.getString(COLUMN_MODULE_NAME);
        this.businessType = json.getString(COLUMN_BUSINESS_TYPE);
        this.prevEmployee = json.getString(COLUMN_PREV_EMPLOYEE);
        this.prevLink = json.getString(COLUMN_PREV_LINK);
        this.prevEmployeeName = json.getString(COLUMN_PREV_EMPLOYEE_NAME);
        this.actorEmployee = json.getString(COLUMN_ACTOR_EMPLOYEE);
        this.actorEmployeeName = json.getString(COLUMN_ACTOR_EMPLOYEE_NAME);
        this.businessId = json.getString(COLUMN_BUSINESS_ID);
        this.extraInfo = json.getString(COLUMN_EXTRA_INFO);
        this.taskId = json.getLong(COLUMN_TASK_ID);
        this.updateTraceId = json.getLong(COLUMN_UPDATE_TRACE_ID);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getPrevLink() {
        return prevLink;
    }

    public void setPrevLink(String prevLink) {
        this.prevLink = prevLink;
    }

    public String getPrevEmployee() {
        return prevEmployee;
    }

    public void setPrevEmployee(String prevEmployee) {
        this.prevEmployee = prevEmployee;
    }

    public String getPrevEmployeeName() {
        return prevEmployeeName;
    }

    public void setPrevEmployeeName(String prevEmployeeName) {
        this.prevEmployeeName = prevEmployeeName;
    }

    public String getActorEmployee() {
        return actorEmployee;
    }

    public void setActorEmployee(String actorEmployee) {
        this.actorEmployee = actorEmployee;
    }

    public String getActorEmployeeName() {
        return actorEmployeeName;
    }

    public void setActorEmployeeName(String actorEmployeeName) {
        this.actorEmployeeName = actorEmployeeName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getUpdateTraceId() {
        return updateTraceId;
    }

    public void setUpdateTraceId(Long updateTraceId) {
        this.updateTraceId = updateTraceId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    public static SqlAssist taskIdSqlAssist(Long taskId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_TASK_ID, taskId);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}
