package com.whatswater.curd.project.sys.employeeFilter;


import java.util.ArrayList;
import java.util.List;

public class EmployeeFilterTreeVo {
    private String code;
    private String remark;
    private List<EmployeeFilterPathValue> pathValueList;

    public EmployeeFilter toFilter() {
        EmployeeFilter employeeFilter = new EmployeeFilter();
        employeeFilter.setCode(code);
        employeeFilter.setRemark(remark);

        return employeeFilter;
    }

    public List<EmployeeFilterData> toFilterDataList() {
        List<EmployeeFilterData> filterList = new ArrayList<>(pathValueList.size());
        for (EmployeeFilterPathValue pathValue: pathValueList) {
            EmployeeFilterData filterData = new EmployeeFilterData();
            filterData.setCode(code);
            filterData.setPath(pathValue.getPath());
            filterData.setValue(pathValue.getValue());

            String path = pathValue.getPath();
            filterData.setValueType(SExpressionUtil.parseValueType(path));
            filterList.add(filterData);
        }

        return filterList;
    }

    public static class EmployeeFilterPathValue {
        String path;
        String value;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<EmployeeFilterPathValue> getPathValueList() {
        return pathValueList;
    }

    public void setPathValueList(List<EmployeeFilterPathValue> pathValueList) {
        this.pathValueList = pathValueList;
    }
}
