package com.whatswater.curd.project.module.todo;


import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.sql.utils.StringUtils;
import io.vertx.ext.sql.assist.SqlAssist;

import javax.ws.rs.QueryParam;
import java.time.LocalDateTime;
import java.util.List;

public class TodoQuery {
    private String title;
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;

    private String moduleName;
    private Integer status;
    private List<Integer> statusList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(LocalDateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public LocalDateTime getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public SqlAssist toSqlAssist() {
        SqlAssist sqlAssist = new SqlAssist();
        if (StringUtils.isNotEmpty(this.getTitle())) {
            sqlAssist.andLike(Todo.COLUMN_TITLE, this.getTitle());
        }
        if (this.getCreateTimeStart() != null) {
            sqlAssist.andGte(Todo.COLUMN_CREATE_TIME, this.getCreateTimeStart());
        }
        if (this.getCreateTimeEnd() != null) {
            sqlAssist.andLte(Todo.COLUMN_CREATE_TIME, this.getCreateTimeEnd());
        }
        if (CrudUtils.gtZero(this.getStatus())) {
            sqlAssist.andEq(Todo.COLUMN_STATUS, this.getStatus());
        }
        if (StringUtils.isNotEmpty(this.getModuleName())) {
            sqlAssist.andLike(Todo.COLUMN_MODULE_NAME, this.getModuleName());
        }
        if (CollectionUtil.isNotEmpty(statusList)) {
            CrudUtils.andIn(sqlAssist, Todo.COLUMN_STATUS, statusList);
        }
        return sqlAssist;
    }
}
