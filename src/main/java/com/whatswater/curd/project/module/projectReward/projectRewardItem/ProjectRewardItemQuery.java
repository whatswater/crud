package com.whatswater.curd.project.module.projectReward.projectRewardItem;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.util.Objects;

public class ProjectRewardItemQuery {
    Integer year;
    Long categoryId;
    String itemName;


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

    public String getItemName() {
        return this.itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (Objects.nonNull(year)) {
            sqlAssist.andEq(ProjectRewardItem.COLUMN_YEAR, year);
        }
        if (Objects.nonNull(categoryId)) {
            sqlAssist.andEq(ProjectRewardItem.COLUMN_CATEGORY_ID, categoryId);
        }
        if (StrUtil.isNotEmpty(itemName)) {
            sqlAssist.andLike(ProjectRewardItem.COLUMN_ITEM_NAME, "%" + itemName + "%");
        }
        return sqlAssist;
    }
}
