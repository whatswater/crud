package com.whatswater.curd.project.sys.attachment;


import cn.hutool.core.util.StrUtil;
import io.vertx.ext.sql.assist.SqlAssist;
import java.time.LocalDateTime;
import java.util.Objects;

public class AttachmentQuery {
    
    String businessType;
    String businessId;
    LocalDateTime createTimeStart;
    LocalDateTime createTimeEnd;
    String fileOldName;
    
    
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
    
    public String getFileOldName() {
        return this.fileOldName;
    }
    
    public void setFileOldName(String fileOldName) {
        this.fileOldName = fileOldName;
    }
    
    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StrUtil.isNotEmpty(businessType)) {
            sqlAssist.andEq(Attachment.COLUMN_BUSINESS_TYPE, businessType);
        }
        if (StrUtil.isNotEmpty(businessId)) {
            sqlAssist.andEq(Attachment.COLUMN_BUSINESS_ID, businessId);
        }
        if (Objects.nonNull(createTimeStart)) {
            sqlAssist.andGte(Attachment.COLUMN_CREATE_TIME, createTimeStart);
        }
        if (Objects.nonNull(createTimeEnd)) {
            sqlAssist.andLte(Attachment.COLUMN_CREATE_TIME, createTimeEnd);
        }
        if (StrUtil.isNotEmpty(fileOldName)) {
            sqlAssist.andLike(Attachment.COLUMN_FILE_OLD_NAME, "%" + fileOldName + "%");
        }
        return sqlAssist;
    }
}
