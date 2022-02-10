package com.whatswater.curd.project.sys.attachment;


import javax.ws.rs.FormParam;

public class UploadVo {
    @FormParam("businessType")
    private String businessType;
    @FormParam("businessId")
    private String businessId;

    public UploadVo() {
    }

    public UploadVo(String businessType, String businessId) {
        this.businessType = businessType;
        this.businessId = businessId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
