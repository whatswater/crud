package com.whatswater.gen;

import java.util.*;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:41
 * @version:1.0
 */
public class ConstructorDefinition implements AddImportClass {
    // public, private protected
    private int modifier;
    List<ParameterDefinition> parameterList;
    private String code;
    private final Set<String> codeImportClass;
    // 构造函数可抛出异常
    private final Set<String> exceptionList;
    private String comment;

    public ConstructorDefinition() {
        this.codeImportClass = new TreeSet<>();
        this.exceptionList = new TreeSet<>();
    }

    public ConstructorDefinition setAccess(AccessTypeEnum access) {
        this.modifier = ModifierUtil.setAccess(modifier, access.getValue());
        return this;
    }

    public int getModifier() {
        return modifier;
    }

    public String getAccessName() {
        return ModifierUtil.getAccessName(modifier);
    }

    public List<ParameterDefinition> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<ParameterDefinition> parameterList) {
        this.parameterList = parameterList;
    }

    public ConstructorDefinition addParameter(ParameterDefinition parameterDefinition) {
        if (parameterList == null) {
            parameterList = new ArrayList<>();
        }
        parameterList.add(parameterDefinition);
        return this;
    }

    public String getCode() {
        return code;
    }

    public ConstructorDefinition setCode(String code) {
        this.code = code;
        return this;
    }

    public Set<String> getCodeImportClass() {
        return codeImportClass;
    }

    public ConstructorDefinition addCodeImportClass(String importPackageName) {
        this.codeImportClass.add(importPackageName);
        return this;
    }

    public ConstructorDefinition addCodeImportClass(String... importPackageName) {
        this.codeImportClass.addAll(Arrays.asList(importPackageName));
        return this;
    }

    public Set<String> getExceptionList() {
        return exceptionList;
    }

    public ConstructorDefinition addThrowException(String exception) {
        this.exceptionList.add(exception);
        this.codeImportClass.add(exception);
        return this;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if (parameterList != null) {
            for (ParameterDefinition parameterDefinition : parameterList) {
                parameterDefinition.dealImport(classNames);
            }
        }
        if (codeImportClass != null) {
            classNames.addAll(codeImportClass);
        }
        if (exceptionList != null) {
            classNames.addAll(exceptionList);
        }
    }
}
