package com.whatswater.curd.project.module.workflow.flowEngine.context;


import java.time.LocalDateTime;
import java.util.Map;

public class ContextFlowInstanceStart {
    private Integer startType;
    private String startUser;
    private String startUserShow;
    private LocalDateTime startTime;
    private Map<String, String> initVariableTable;

    public Integer getStartType() {
        return startType;
    }

    public void setStartType(Integer startType) {
        this.startType = startType;
    }

    public String getStartUser() {
        return startUser;
    }

    public void setStartUser(String startUser) {
        this.startUser = startUser;
    }

    public String getStartUserShow() {
        return startUserShow;
    }

    public void setStartUserShow(String startUserShow) {
        this.startUserShow = startUserShow;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Map<String, String> getInitVariableTable() {
        return initVariableTable;
    }

    public void setInitVariableTable(Map<String, String> initVariableTable) {
        this.initVariableTable = initVariableTable;
    }
}
