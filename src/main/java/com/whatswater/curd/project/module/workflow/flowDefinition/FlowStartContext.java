package com.whatswater.curd.project.module.workflow.flowDefinition;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class FlowStartContext {
    Integer startType;
    String startUser;
    LocalDateTime startTime;
    Map<String, String> initVariableTable;

    public Integer getStartType() {
        return startType;
    }

    public FlowStartContext setStartType(Integer startType) {
        this.startType = startType;
        return this;
    }

    public String getStartUser() {
        return startUser;
    }

    public FlowStartContext setStartUser(String startUser) {
        this.startUser = startUser;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public FlowStartContext setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Map<String, String> getInitVariableTable() {
        return initVariableTable;
    }

    public FlowStartContext setInitVariableTable(Map<String, String> initVariableTable) {
        this.initVariableTable = initVariableTable;
        return this;
    }

    public FlowStartContext addVariable(String name, String value) {
        if (this.initVariableTable == null) {
            this.initVariableTable = new TreeMap<>();
        }
        this.initVariableTable.put(name, value);
        return this;
    }
}
