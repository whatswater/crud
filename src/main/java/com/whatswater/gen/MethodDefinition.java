package com.whatswater.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:41
 * @version:1.0
 */
public class MethodDefinition implements AddImportClass {
    // public, private protected static final synchronized
    private int modifier;

    // 方法名称
    private String name;
    // 参数列表
    List<ParameterDefinition> parameterList;
    // 抛出的异常列表
    private List<String> exceptionList;
    private JavaType returnType;
    // 泛型
    private JavaType[] generics;

    // 需要导入的包
    private Set<String> importPackageNames;
    // 方法体
    private String code;
    private List<AnnotationDefinition> annotationList;
    private String comment;

    public MethodDefinition setFinal(boolean value) {
        if (value) {
            modifier = ModifierUtil.setFinal(modifier);
        }
        else {
            modifier = ModifierUtil.setNotFinal(modifier);
        }
        return this;
    }

    public boolean isFinal() {
        return ModifierUtil.isFinal(modifier);
    }

    public MethodDefinition setStatic(boolean value) {
        if (value) {
            modifier = ModifierUtil.setStatic(modifier);
        }
        else {
            modifier = ModifierUtil.setNotStatic(modifier);
        }
        return this;
    }

    public boolean isStatic() {
        return ModifierUtil.isStatic(modifier);
    }

    public MethodDefinition setAccess(AccessTypeEnum access) {
        this.modifier = ModifierUtil.setAccess(modifier, access.getValue());
        return this;
    }

    public String getAccessName() {
        return ModifierUtil.getAccessName(modifier);
    }

    public MethodDefinition setSynchronized(boolean value) {
        if (value) {
            modifier = ModifierUtil.setSynchronized(modifier);
        }
        else {
            modifier = ModifierUtil.setNotSynchronized(modifier);
        }
        return this;
    }

    public boolean isSynchronized() {
        return ModifierUtil.isSynchronized(modifier);
    }

    public String getName() {
        return name;
    }

    public MethodDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public MethodDefinition setCode(String code) {
        this.code = code;
        return this;
    }

    public JavaType getReturnType() {
        return returnType;
    }

    public MethodDefinition setReturnType(JavaType returnType) {
        this.returnType = returnType;
        return this;
    }

    public JavaType[] getGenerics() {
        return generics;
    }

    public MethodDefinition setGenerics(JavaType[] generics) {
        this.generics = generics;
        return this;
    }

    public List<String> getExceptionList() {
        return exceptionList;
    }

    public MethodDefinition addException(String exceptionClassName) {
        if (exceptionList == null) {
            exceptionList = new ArrayList<>();
        }
        exceptionList.add(exceptionClassName);
        return this;
    }

    public List<ParameterDefinition> getParameterList() {
        return parameterList;
    }

    public MethodDefinition addParameter(ParameterDefinition parameterDefinition) {
        if (parameterList == null) {
            parameterList = new ArrayList<>();
        }
        parameterList.add(parameterDefinition);
        return this;
    }

    public List<AnnotationDefinition> getAnnotationList() {
        return annotationList;
    }

    public MethodDefinition addAnnotation(AnnotationDefinition annotationDefinition) {
        if (annotationList == null) {
            annotationList = new ArrayList<>();
        }
        annotationList.add(annotationDefinition);
        return this;
    }

    public MethodDefinition addImportClass(String className) {
        if (importPackageNames == null) {
            importPackageNames = new TreeSet<>();
        }
        importPackageNames.add(className);
        return this;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if (parameterList != null) {
            for (ParameterDefinition parameterDefinition : parameterList) {
                parameterDefinition.dealImport(classNames);
            }
        }
        if (exceptionList != null) {
            classNames.addAll(exceptionList);
        }
        if (returnType != null) {
            returnType.dealImport(classNames);
        }
        if (generics != null) {
            for (JavaType javaType : generics) {
                javaType.dealImport(classNames);
            }
        }
        if (importPackageNames != null) {
            classNames.addAll(importPackageNames);
        }
        if (annotationList != null) {
            for (AnnotationDefinition annotationDefinition : annotationList) {
                annotationDefinition.dealImport(classNames);
            }
        }
    }
}
