package com.whatswater.curd.project.module.workflow.event;

import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;

import java.util.List;
import java.util.Map;

public class GenerateTaskContext {
    private List<FlowInstanceTask> flowInstanceTask;
    private Map<String, String> instanceVariable;

    public List<FlowInstanceTask> getFlowInstanceTask() {
        return flowInstanceTask;
    }

    public void setFlowInstanceTask(List<FlowInstanceTask> flowInstanceTask) {
        this.flowInstanceTask = flowInstanceTask;
    }

    public Map<String, String> getInstanceVariable() {
        return instanceVariable;
    }

    public void setInstanceVariable(Map<String, String> instanceVariable) {
        this.instanceVariable = instanceVariable;
    }

    public String getFlowVariableValue(String variableName) {
        if (instanceVariable == null) {
            return null;
        }

        return instanceVariable.get(variableName);
    }
}
