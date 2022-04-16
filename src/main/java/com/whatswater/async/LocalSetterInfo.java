package com.whatswater.async;


import org.objectweb.asm.Type;

public class LocalSetterInfo {
    private String name;
    private String desc;

    private String setterName;
    private String setterDesc;

    public LocalSetterInfo(String name, String desc, String setterName, String setterDesc) {
        this.name = name;
        this.desc = desc;
        this.setterName = setterName;
        this.setterDesc = setterDesc;
    }

    public Type getType() {
        return Type.getType(desc);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSetterName() {
        return setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public String getSetterDesc() {
        return setterDesc;
    }

    public void setSetterDesc(String setterDesc) {
        this.setterDesc = setterDesc;
    }
}
