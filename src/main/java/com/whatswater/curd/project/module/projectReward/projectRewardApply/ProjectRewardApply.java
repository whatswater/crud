package com.whatswater.curd.project.module.projectReward.projectRewardApply;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import io.vertx.sqlclient.data.Numeric;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Table("b_project_reward_apply")
public class ProjectRewardApply {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APPLY_NO = "apply_no";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_CONTENT_ID = "content_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_CATEGORY_TOTAL_COST = "category_total_cost";
    public static final String COLUMN_CONTENT_TOTAL_COST = "content_total_cost";
    public static final String COLUMN_APPLY_ORGANIZATION_CODE = "apply_organization_code";
    public static final String COLUMN_APPLY_ORGANIZATION_NAME = "apply_organization_name";
    public static final String COLUMN_ALLOC_ORGANIZATION_CODE = "alloc_organization_code";
    public static final String COLUMN_ALLOC_ORGANIZATION_NAME = "alloc_organization_name";
    public static final String COLUMN_REWARD_STANDARD = "reward_standard";
    public static final String COLUMN_SYSTEM_BASIS = "system_basis";
    public static final String COLUMN_APPLY_CONTENT = "apply_content";
    public static final String COLUMN_APPLY_REMARK = "apply_remark";
    public static final String COLUMN_ATTACHMENT = "attachment";
    public static final String COLUMN_APPLY_MONEY = "apply_money";
    public static final String COLUMN_CHECK_MONEY = "check_money";
    public static final String COLUMN_CHECK_REMARK = "check_remark";
    public static final String COLUMN_APPLICANT_LOGIN_NAME = "applicant_login_name";
    public static final String COLUMN_APPLICANT_NAME = "applicant_name";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_FIRST_COMMIT_TIME = "first_commit_time";
    public static final String COLUMN_STATUS = "status";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_APPLY_NO)
    String applyNo;
    @TableColumn(COLUMN_TITLE)
    String title;
    @TableColumn(COLUMN_YEAR)
    Integer year;
    @TableColumn(COLUMN_CATEGORY_ID)
    Long categoryId;
    @TableColumn(COLUMN_CATEGORY)
    String category;
    @TableColumn(COLUMN_CONTENT_ID)
    Long contentId;
    @TableColumn(COLUMN_CONTENT)
    String content;
    @TableColumn(COLUMN_ITEM_ID)
    Long itemId;
    @TableColumn(COLUMN_ITEM)
    String item;
    @TableColumn(COLUMN_CATEGORY_TOTAL_COST)
    BigDecimal categoryTotalCost;
    @TableColumn(COLUMN_CONTENT_TOTAL_COST)
    BigDecimal contentTotalCost;
    @TableColumn(COLUMN_APPLY_ORGANIZATION_CODE)
    String applyOrganizationCode;
    @TableColumn(COLUMN_APPLY_ORGANIZATION_NAME)
    String applyOrganizationName;
    @TableColumn(COLUMN_ALLOC_ORGANIZATION_CODE)
    String allocOrganizationCode;
    @TableColumn(COLUMN_ALLOC_ORGANIZATION_NAME)
    String allocOrganizationName;
    @TableColumn(COLUMN_REWARD_STANDARD)
    String rewardStandard;
    @TableColumn(COLUMN_SYSTEM_BASIS)
    String systemBasis;
    @TableColumn(COLUMN_APPLY_CONTENT)
    String applyContent;
    @TableColumn(COLUMN_APPLY_REMARK)
    String applyRemark;
    @TableColumn(COLUMN_ATTACHMENT)
    String attachment;
    @TableColumn(COLUMN_APPLY_MONEY)
    BigDecimal applyMoney;
    @TableColumn(COLUMN_CHECK_MONEY)
    BigDecimal checkMoney;
    @TableColumn(COLUMN_CHECK_REMARK)
    String checkRemark;
    @TableColumn(COLUMN_APPLICANT_LOGIN_NAME)
    String applicantLoginName;
    @TableColumn(COLUMN_APPLICANT_NAME)
    String applicantName;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;
    @TableColumn(COLUMN_FIRST_COMMIT_TIME)
    LocalDateTime firstCommitTime;
    @TableColumn(COLUMN_STATUS)
    Integer status;


    public ProjectRewardApply() {

    }

    public ProjectRewardApply(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.applyNo = json.getString(COLUMN_APPLY_NO);
        this.title = json.getString(COLUMN_TITLE);
        this.year = json.getInteger(COLUMN_YEAR);
        this.categoryId = json.getLong(COLUMN_CATEGORY_ID);
        this.category = json.getString(COLUMN_CATEGORY);
        this.contentId = json.getLong(COLUMN_CONTENT_ID);
        this.content = json.getString(COLUMN_CONTENT);
        this.itemId = json.getLong(COLUMN_ITEM_ID);
        this.item = json.getString(COLUMN_ITEM);
        this.categoryTotalCost = ((Numeric) json.getValue(COLUMN_CATEGORY_TOTAL_COST)).bigDecimalValue();
        this.contentTotalCost = ((Numeric) json.getValue(COLUMN_CONTENT_TOTAL_COST)).bigDecimalValue();
        this.applyOrganizationCode = json.getString(COLUMN_APPLY_ORGANIZATION_CODE);
        this.applyOrganizationName = json.getString(COLUMN_APPLY_ORGANIZATION_NAME);
        this.allocOrganizationCode = json.getString(COLUMN_ALLOC_ORGANIZATION_CODE);
        this.allocOrganizationName = json.getString(COLUMN_ALLOC_ORGANIZATION_NAME);
        this.rewardStandard = json.getString(COLUMN_REWARD_STANDARD);
        this.systemBasis = json.getString(COLUMN_SYSTEM_BASIS);
        this.applyContent = json.getString(COLUMN_APPLY_CONTENT);
        this.applyRemark = json.getString(COLUMN_APPLY_REMARK);
        this.attachment = json.getString(COLUMN_ATTACHMENT);

        this.applyMoney = ((Numeric) json.getValue(COLUMN_APPLY_MONEY)).bigDecimalValue();
        this.checkMoney = ((Numeric) json.getValue(COLUMN_CHECK_MONEY)).bigDecimalValue();

        this.checkRemark = json.getString(COLUMN_CHECK_REMARK);
        this.applicantLoginName = json.getString(COLUMN_APPLICANT_LOGIN_NAME);
        this.applicantName = json.getString(COLUMN_APPLICANT_NAME);
        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
        String firstCommitTime = json.getString(COLUMN_FIRST_COMMIT_TIME);
        this.firstCommitTime = StrUtil.isEmpty(firstCommitTime) ? null : CrudUtils.parseSqlDateTimeFormat(firstCommitTime);
        this.status = json.getInteger(COLUMN_STATUS);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItem() {
        return this.item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getCategoryTotalCost() {
        return categoryTotalCost;
    }

    public void setCategoryTotalCost(BigDecimal categoryTotalCost) {
        this.categoryTotalCost = categoryTotalCost;
    }

    public BigDecimal getContentTotalCost() {
        return contentTotalCost;
    }

    public void setContentTotalCost(BigDecimal contentTotalCost) {
        this.contentTotalCost = contentTotalCost;
    }

    public String getApplyOrganizationCode() {
        return this.applyOrganizationCode;
    }

    public void setApplyOrganizationCode(String applyOrganizationCode) {
        this.applyOrganizationCode = applyOrganizationCode;
    }

    public String getApplyOrganizationName() {
        return this.applyOrganizationName;
    }

    public void setApplyOrganizationName(String applyOrganizationName) {
        this.applyOrganizationName = applyOrganizationName;
    }

    public String getAllocOrganizationCode() {
        return this.allocOrganizationCode;
    }

    public void setAllocOrganizationCode(String allocOrganizationCode) {
        this.allocOrganizationCode = allocOrganizationCode;
    }

    public String getAllocOrganizationName() {
        return this.allocOrganizationName;
    }

    public void setAllocOrganizationName(String allocOrganizationName) {
        this.allocOrganizationName = allocOrganizationName;
    }

    public String getRewardStandard() {
        return this.rewardStandard;
    }

    public void setRewardStandard(String rewardStandard) {
        this.rewardStandard = rewardStandard;
    }

    public String getSystemBasis() {
        return this.systemBasis;
    }

    public void setSystemBasis(String systemBasis) {
        this.systemBasis = systemBasis;
    }

    public String getApplyContent() {
        return this.applyContent;
    }

    public void setApplyContent(String applyContent) {
        this.applyContent = applyContent;
    }

    public String getApplyRemark() {
        return this.applyRemark;
    }

    public void setApplyRemark(String applyRemark) {
        this.applyRemark = applyRemark;
    }

    public String getAttachment() {
        return this.attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public BigDecimal getApplyMoney() {
        return this.applyMoney;
    }

    public void setApplyMoney(BigDecimal applyMoney) {
        this.applyMoney = applyMoney;
    }

    public BigDecimal getCheckMoney() {
        return this.checkMoney;
    }

    public void setCheckMoney(BigDecimal checkMoney) {
        this.checkMoney = checkMoney;
    }

    public String getCheckRemark() {
        return this.checkRemark;
    }

    public void setCheckRemark(String checkRemark) {
        this.checkRemark = checkRemark;
    }

    public String getApplicantLoginName() {
        return this.applicantLoginName;
    }

    public void setApplicantLoginName(String applicantLoginName) {
        this.applicantLoginName = applicantLoginName;
    }

    public String getApplicantName() {
        return this.applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getFirstCommitTime() {
        return this.firstCommitTime;
    }

    public void setFirstCommitTime(LocalDateTime firstCommitTime) {
        this.firstCommitTime = firstCommitTime;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public static SqlAssist applyNoSqlAssist(String applyNo) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_APPLY_NO, applyNo);
        sqlAssist.setRowSize(1);

        return sqlAssist;
    }
}
