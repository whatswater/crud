package com.whatswater.curd.project.module.workflow.flowEngine;


import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkEvent;

public class FlowLinkEventWithTrigger extends FlowLinkEvent {
    private String triggerName;

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public FlowLinkEvent toFlowLinkEvent() {
        FlowLinkEvent flowLinkEvent = new FlowLinkEvent();
        flowLinkEvent.setType(this.getType());
        flowLinkEvent.setEndpoint(this.getEndpoint());

        return flowLinkEvent;
    }
}
