package com.whatswater.curd.project.module.workflow.flowEngine;


import java.util.List;

public class NextLinkActorInfo {
    private Long nextLinkId;
    private String nextLinkCode;
    private List<String> actorList;
    private List<String> actorNameList;

    public Long getNextLinkId() {
        return nextLinkId;
    }

    public void setNextLinkId(Long nextLinkId) {
        this.nextLinkId = nextLinkId;
    }

    public String getNextLinkCode() {
        return nextLinkCode;
    }

    public void setNextLinkCode(String nextLinkCode) {
        this.nextLinkCode = nextLinkCode;
    }

    public List<String> getActorList() {
        return actorList;
    }

    public void setActorList(List<String> actorList) {
        this.actorList = actorList;
    }

    public List<String> getActorNameList() {
        return actorNameList;
    }

    public void setActorNameList(List<String> actorNameList) {
        this.actorNameList = actorNameList;
    }
}
