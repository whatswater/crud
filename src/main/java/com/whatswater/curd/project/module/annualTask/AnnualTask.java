package com.whatswater.curd.project.module.annualTask;

import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Table("b_annual_task")
public class AnnualTask {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_NO = "no";
    public static final String COLUMN_GOAL = "goal";
    public static final String COLUMN_SOURCE_TYPE = "source_type";
    public static final String COLUMN_MAKE_DATE = "make_date";
    public static final String COLUMN_VERSION_NO = "version_no";
    public static final String COLUMN_STATUS = "status";

    @TableId(COLUMN_ID)
    private Long id;
    @TableColumn(COLUMN_YEAR)
    private Integer year;
    @TableColumn(COLUMN_CONTENT)
    private String content;
    @TableColumn(COLUMN_PARENT_ID)
    private Long parentId;
    @TableColumn(COLUMN_LEVEL)
    private Integer level;
    @TableColumn(COLUMN_NO)
    private Integer no;
    @TableColumn(COLUMN_GOAL)
    private String goal;
    @TableColumn(COLUMN_SOURCE_TYPE)
    private Integer sourceType;
    @TableColumn(COLUMN_MAKE_DATE)
    private LocalDate makeDate;
    @TableColumn(COLUMN_VERSION_NO)
    private Integer versionNo;
    @TableColumn(COLUMN_STATUS)
    private Integer status;

    public AnnualTask() {
    }

    public AnnualTask(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.year = json.getInteger(COLUMN_YEAR);
        this.content = json.getString(COLUMN_CONTENT);
        this.parentId = json.getLong(COLUMN_PARENT_ID);
        this.level = json.getInteger(COLUMN_LEVEL);
        this.no = json.getInteger(COLUMN_NO);
        this.goal = json.getString(COLUMN_GOAL);
        this.sourceType = json.getInteger(COLUMN_SOURCE_TYPE);
        String makeDate = json.getString(COLUMN_MAKE_DATE);
        this.makeDate = StrUtil.isEmpty(makeDate)  ? null : CrudUtils.parseDate(makeDate);
        this.versionNo = json.getInteger(COLUMN_VERSION_NO);
        this.status = json.getInteger(COLUMN_STATUS);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public LocalDate getMakeDate() {
        return makeDate;
    }

    public void setMakeDate(LocalDate makeDate) {
        this.makeDate = makeDate;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean canUpdate() {
        return AnnualTaskStatus.INIT.getId().equals(getStatus()) || AnnualTaskStatus.IN_REVIEW.getId().equals(getStatus());
    }

    public static SqlAssist idSqlAssist(Long id) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_ID, id);
        sqlAssist.setRowSize(1);
        return sqlAssist;
    }
}
