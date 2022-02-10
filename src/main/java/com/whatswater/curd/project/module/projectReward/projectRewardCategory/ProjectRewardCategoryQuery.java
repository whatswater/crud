package com.whatswater.curd.project.module.projectReward.projectRewardCategory;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.util.Objects;

public class ProjectRewardCategoryQuery {

    Integer year;
    String category;
    Integer level;


    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(year)) {
            sqlAssist.andEq(ProjectRewardCategory.COLUMN_YEAR, year);
        }
        if (StrUtil.isNotEmpty(category)) {
            sqlAssist.andLike(ProjectRewardCategory.COLUMN_CATEGORY, "%" + category + "%");
        }
        if (Objects.nonNull(level)) {
            sqlAssist.andEq(ProjectRewardCategory.COLUMN_LEVEL, level);
        }
        return sqlAssist;
    }
}
