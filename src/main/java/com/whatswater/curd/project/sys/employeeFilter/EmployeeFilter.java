package com.whatswater.curd.project.sys.employeeFilter;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.ext.sql.assist.Table;
import io.vertx.ext.sql.assist.TableColumn;
import io.vertx.ext.sql.assist.TableId;

import java.time.LocalDateTime;

@Table("sys_employee_filter")
public class EmployeeFilter {
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_REMARK = "remark";

    @TableId(COLUMN_ID)
    Long id;
    @TableColumn(COLUMN_CODE)
    String code;
    @TableColumn(COLUMN_CREATE_TIME)
    LocalDateTime createTime;
    @TableColumn(COLUMN_REMARK)
    String remark;


    public EmployeeFilter() {

    }

    public EmployeeFilter(JsonObject json) {
        this.id = json.getLong(COLUMN_ID);
        this.code = json.getString(COLUMN_CODE);

        String createTime = json.getString(COLUMN_CREATE_TIME);
        this.createTime = StrUtil.isEmpty(createTime) ? null : CrudUtils.parseSqlDateTimeFormat(createTime);
        this.remark = json.getString(COLUMN_REMARK);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static SqlAssist codeSqlAssist(String code) {
        SqlAssist sqlAssist = new SqlAssist();
        sqlAssist.andEq(COLUMN_CODE, code);

        return sqlAssist;
    }
}
