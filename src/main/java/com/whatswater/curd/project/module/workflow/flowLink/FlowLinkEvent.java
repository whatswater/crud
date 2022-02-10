package com.whatswater.curd.project.module.workflow.flowLink;



public class FlowLinkEvent {
    private String type;
    private String endpoint;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
