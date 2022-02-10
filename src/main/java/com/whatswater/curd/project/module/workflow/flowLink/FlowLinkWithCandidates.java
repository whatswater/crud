package com.whatswater.curd.project.module.workflow.flowLink;


import cn.hutool.core.bean.BeanUtil;
import com.whatswater.curd.project.sys.employee.Employee;

import java.util.List;

public class FlowLinkWithCandidates extends FlowLink {
    private List<Candidate> candidates;
    private boolean multiSelect;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public static class Candidate {
        private String name;
        private String loginName;
        private boolean checked;
        private boolean readonly;

        public static Candidate fromEmployee(Employee employee) {
            Candidate candidate = new Candidate();
            candidate.loginName = employee.getLoginName();
            candidate.name = employee.getName();
            candidate.checked = false;
            candidate.readonly = false;
            return candidate;
        }

        public static Candidate fromWorkflowAssign(String loginName) {
            Candidate candidate = new Candidate();

            candidate.loginName = loginName;
            candidate.checked = true;
            candidate.readonly = true;

            return candidate;
        }

        public Candidate() {

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }
    }

    public static FlowLinkWithCandidates fromFlowLink(FlowLink flowLink) {
        return BeanUtil.copyProperties(flowLink, FlowLinkWithCandidates.class);
    }
}
