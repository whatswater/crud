package com.whatswater.curd.project.sys.attachment;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Table("sys_attachment")
public class Attachment {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BUSINESS_TYPE = "business_type";
    public static final String COLUMN_BUSINESS_ID = "business_id";
    public static final String COLUMN_FILE_OLD_NAME = "file_old_name";
    public static final String COLUMN_CONTENT_SIZE = "content_size";
    public static final String COLUMN_RELATIVE_FILE_PATH = "relative_file_path";
    public static final String COLUMN_CREATE_TIME = "create_time";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_BUSINESS_TYPE)
    String businessType;
    @TableColumn(COLUMN_BUSINESS_ID)
    String businessId;
    @TableColumn(COLUMN_FILE_OLD_NAME)
    String fileOldName;
    @TableColumn(COLUMN_CONTENT_SIZE)
    Long contentSize;
    @TableColumn(COLUMN_RELATIVE_FILE_PATH)
    String relativeFilePath;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;


    public Attachment() {

    }

    public Attachment(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.businessType = json.getString(COLUMN_BUSINESS_TYPE);
        this.businessId = json.getString(COLUMN_BUSINESS_ID);
        this.fileOldName = json.getString(COLUMN_FILE_OLD_NAME);
        this.contentSize = json.getLong(COLUMN_CONTENT_SIZE);
        this.relativeFilePath = json.getString(COLUMN_RELATIVE_FILE_PATH);
        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return this.businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFileOldName() {
        return this.fileOldName;
    }

    public void setFileOldName(String fileOldName) {
        this.fileOldName = fileOldName;
    }

    public Long getContentSize() {
        return this.contentSize;
    }

    public void setContentSize(Long contentSize) {
        this.contentSize = contentSize;
    }

    public String getRelativeFilePath() {
        return this.relativeFilePath;
    }

    public void setRelativeFilePath(String relativeFilePath) {
        this.relativeFilePath = relativeFilePath;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static SqlAssist idListSqlAssist(List<Long> idList) {
        SqlAssist sqlAssist = new SqlAssist();
        CrudUtils.andIn(sqlAssist, COLUMN_ID, idList);
        return sqlAssist;
    }

    public static SqlAssist businessTypeAndIdSqlAssist(String businessType, String businessId) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_BUSINESS_TYPE, businessType);
        sqlAssist.andEq(COLUMN_BUSINESS_ID, businessId);
        return sqlAssist;
    }
}
