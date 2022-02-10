package com.whatswater.curd.project.module.annualTask;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;

import java.time.LocalDate;
import java.util.Objects;

public class AnnualTaskQuery {
    private Integer year;
    private String content;
    private String goal;
    private Integer sourceType;
    private LocalDate makeDateStart;
    private LocalDate makeDateEnd;
    private Integer status;

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

    public LocalDate getMakeDateStart() {
        return makeDateStart;
    }

    public void setMakeDateStart(LocalDate makeDateStart) {
        this.makeDateStart = makeDateStart;
    }

    public LocalDate getMakeDateEnd() {
        return makeDateEnd;
    }

    public void setMakeDateEnd(LocalDate makeDateEnd) {
        this.makeDateEnd = makeDateEnd;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(year)) {
            sqlAssist.andEq(AnnualTask.COLUMN_YEAR, year);
        }
if (StrUtil.isNotEmpty(content)) {
    sqlAssist.andLike(AnnualTask.COLUMN_CONTENT, "%" + content + "%");
}
        if (Objects.nonNull(goal)) {
            sqlAssist.andLike(AnnualTask.COLUMN_GOAL, "%" + goal + "%");
        }
        if (Objects.nonNull(sourceType)) {
            sqlAssist.andEq(AnnualTask.COLUMN_SOURCE_TYPE, sourceType);
        }
        if (Objects.nonNull(makeDateStart)) {
            sqlAssist.andGte(AnnualTask.COLUMN_MAKE_DATE, makeDateStart);
        }
        if (Objects.nonNull(makeDateEnd)) {
            sqlAssist.andLte(AnnualTask.COLUMN_MAKE_DATE, makeDateEnd);
        }
        if (Objects.nonNull(status)) {
            sqlAssist.andEq(AnnualTask.COLUMN_STATUS, status);
        }
        return sqlAssist;
    }
}
