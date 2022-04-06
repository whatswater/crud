package com.whatswater.nothing.data;


import com.whatswater.nothing.property.Properties;

import java.util.List;

public class ModelDataList {
    public static final ModelDataList EMPTY = new ModelDataList();

    private Properties properties;
    private List<Object[]> dataList;

    public ModelDataList() {
    }

    public ModelDataList(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<Object[]> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object[]> dataList) {
        this.dataList = dataList;
    }
}
