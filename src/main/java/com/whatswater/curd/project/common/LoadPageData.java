package com.whatswater.curd.project.common;


import java.util.ArrayList;
import java.util.List;

public class LoadPageData {
    private List<String> permissionList;
    private List<Dict> dictData;

    public LoadPageData() {
    }

    public LoadPageData(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    public void setDictData(List<Dict> dictData) {
        this.dictData = dictData;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public List<Dict> getDictData() {
        return dictData;
    }

    public LoadPageData addDict(String type, List<DictItem> dictList) {
        if (this.dictData == null) {
            this.dictData = new ArrayList<>();
        }
        Dict dict = new Dict();
        dict.type = type;
        dict.dictList = dictList;
        this.dictData.add(dict);

        return this;
    }

    public static LoadPageData of(List<String> permissionList) {
        return new LoadPageData(permissionList);
    }

    public static class Dict {
        String type;
        List<DictItem> dictList;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<DictItem> getDictList() {
            return dictList;
        }

        public void setDictList(List<DictItem> dictList) {
            this.dictList = dictList;
        }
    }

    public static class DictItem {
        String value;
        String text;

        public DictItem() {
        }

        public DictItem(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public static DictItem of(String value, String text) {
            return new DictItem(value, text);
        }
    }
}
