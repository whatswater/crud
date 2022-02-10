package com.whatswater.curd.project.module.projectReward.projectRewardItem;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Table("b_project_reward_item")
public class ProjectRewardItem {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_ITEM_NAME = "item_name";
    public static final String COLUMN_STANDARD = "standard";
    public static final String COLUMN_ALLOC_DEPARTMENT_IDS = "alloc_department_ids";
    public static final String COLUMN_ALLOC_DEPARTMENT_NAMES = "alloc_department_names";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_YEAR)
    Integer year;
    @TableColumn(COLUMN_CATEGORY_ID)
    Long categoryId;
    @TableColumn(COLUMN_ITEM_NAME)
    String itemName;
    @TableColumn(COLUMN_STANDARD)
    String standard;
    @TableColumn(COLUMN_ALLOC_DEPARTMENT_IDS)
    String allocDepartmentIds;
    @TableColumn(COLUMN_ALLOC_DEPARTMENT_NAMES)
    String allocDepartmentNames;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;

    public ProjectRewardItem() {

    }

    public ProjectRewardItem(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.year = json.getInteger(COLUMN_YEAR);
        this.categoryId = json.getLong(COLUMN_CATEGORY_ID);
        this.itemName = json.getString(COLUMN_ITEM_NAME);
        this.standard = json.getString(COLUMN_STANDARD);
        this.allocDepartmentIds = json.getString(COLUMN_ALLOC_DEPARTMENT_IDS);
        this.allocDepartmentNames = json.getString(COLUMN_ALLOC_DEPARTMENT_NAMES);
        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStandard() {
        return this.standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getAllocDepartmentIds() {
        return this.allocDepartmentIds;
    }

    public void setAllocDepartmentIds(String allocDepartmentIds) {
        this.allocDepartmentIds = allocDepartmentIds;
    }

    public String getAllocDepartmentNames() {
        return this.allocDepartmentNames;
    }

    public void setAllocDepartmentNames(String allocDepartmentNames) {
        this.allocDepartmentNames = allocDepartmentNames;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
