package com.whatswater.curd.project.module.projectReward.projectRewardCategory;


import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table("b_project_reward_category")
public class ProjectRewardCategory {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TOTAL_COST = "total_cost";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_PARENT_ID)
    private Long parentId;
    @TableColumn(COLUMN_YEAR)
    Integer year;
    @TableColumn(COLUMN_CATEGORY)
    String category;
    @TableColumn(COLUMN_TOTAL_COST)
    BigDecimal totalCost;
    @TableColumn(COLUMN_LEVEL)
    Integer level;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public ProjectRewardCategory() {

    }

    public ProjectRewardCategory(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.parentId = json.getLong(COLUMN_PARENT_ID);
        this.year = json.getInteger(COLUMN_YEAR);
        this.category = json.getString(COLUMN_CATEGORY);
        this.totalCost = ((Numeric) json.getValue(COLUMN_TOTAL_COST)).bigDecimalValue();
        this.level = json.getInteger(COLUMN_LEVEL);

        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

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

    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist categoryYearSqlAssist(String category, Integer year) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_CATEGORY, category);
        sqlAssist.andEq(COLUMN_YEAR, year);

        return sqlAssist;
    }

    public static SqlAssist parentIdSqlAssist(Long parentId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_PARENT_ID, parentId);
        return sqlAssist;
    }

    public static SqlAssist parentIdListSqlAssist(List<Long> parentIdList) {
        SqlAssist sqlAssist = new SqlAssist();
        CrudUtils.andIn(sqlAssist, COLUMN_PARENT_ID, parentIdList);
        return sqlAssist;
    }

    public static SqlAssist idListSqlAssist(List<Long> idList) {
        SqlAssist sqlAssist = new SqlAssist();
        CrudUtils.andIn(sqlAssist, COLUMN_ID, idList);
        return sqlAssist;
    }
}
