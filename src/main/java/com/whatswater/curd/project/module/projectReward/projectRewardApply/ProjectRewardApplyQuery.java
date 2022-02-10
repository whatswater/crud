package com.whatswater.curd.project.module.projectReward.projectRewardApply;

import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProjectRewardApplyQuery {
    Integer year;
    Long categoryId;
    Long itemId;
    String applicantLoginName;
    String title;
    LocalDateTime createTimeStart;
    LocalDateTime createTimeEnd;
    LocalDateTime firstCommitTimeStart;
    LocalDateTime firstCommitTimeEnd;

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getApplicantLoginName() {
        return this.applicantLoginName;
    }

    public void setApplicantLoginName(String applicantLoginName) {
        this.applicantLoginName = applicantLoginName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public LocalDateTime getFirstCommitTimeStart() {
        return this.firstCommitTimeStart;
    }

    public void setFirstCommitTimeStart(LocalDateTime firstCommitTimeStart) {
        this.firstCommitTimeStart = firstCommitTimeStart;
    }

    public LocalDateTime getFirstCommitTimeEnd() {
        return this.firstCommitTimeEnd;
    }

    public void setFirstCommitTimeEnd(LocalDateTime firstCommitTimeEnd) {
        this.firstCommitTimeEnd = firstCommitTimeEnd;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(year)) {
            sqlAssist.andEq(ProjectRewardApply.COLUMN_YEAR, year);
        }
        if (Objects.nonNull(categoryId)) {
            sqlAssist.andEq(ProjectRewardApply.COLUMN_CATEGORY_ID, categoryId);
        }
        if (Objects.nonNull(itemId)) {
            sqlAssist.andEq(ProjectRewardApply.COLUMN_ITEM_ID, itemId);
        }
        if (StrUtil.isNotEmpty(applicantLoginName)) {
            sqlAssist.andEq(ProjectRewardApply.COLUMN_APPLICANT_LOGIN_NAME, applicantLoginName);
        }
        if (StrUtil.isNotEmpty(title)) {
            sqlAssist.andLike(ProjectRewardApply.COLUMN_TITLE, "%" + title + "%");
        }
        if (Objects.nonNull(createTimeStart)) {
            sqlAssist.andGte(ProjectRewardApply.COLUMN_CREATE_TIME, createTimeStart);
        }
        if (Objects.nonNull(createTimeEnd)) {
            sqlAssist.andLte(ProjectRewardApply.COLUMN_CREATE_TIME, createTimeEnd);
        }
        if (Objects.nonNull(firstCommitTimeStart)) {
            sqlAssist.andGte(ProjectRewardApply.COLUMN_FIRST_COMMIT_TIME, firstCommitTimeStart);
        }
        if (Objects.nonNull(firstCommitTimeEnd)) {
            sqlAssist.andLte(ProjectRewardApply.COLUMN_FIRST_COMMIT_TIME, firstCommitTimeEnd);
        }
        return sqlAssist;
    }
}
