package com.whatswater.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:40
 * @version:1.0
 */
public class FieldDefinition implements AddImportClass {
    private String name;
    // java的类型签名
    private JavaType type;
    // public, private protected static final volatile transient标记
    private int modifier;
    private String initExpression;
    private Set<String> importPackageNames;
    private List<AnnotationDefinition> annotationList;
    private String comment;

    public FieldDefinition() {
        this.modifier = 0;
    }

    public FieldDefinition(String name) {
        this();
        this.name = name;
    }

    public FieldDefinition setTransient(boolean value) {
        if (value) {
            modifier = ModifierUtil.setTransient(modifier);
        }
        else {
            modifier = ModifierUtil.setNotTransient(modifier);
        }
        return this;
    }

    public boolean isTransient() {
        return ModifierUtil.isTransient(modifier);
    }

    public FieldDefinition setVolatile(boolean value) {
        if (value) {
            modifier = ModifierUtil.setVolatile(modifier);
        }
        else {
            modifier = ModifierUtil.setNotVolatile(modifier);
        }
        return this;
    }

    public boolean isVolatile() {
        return ModifierUtil.isVolatile(modifier);
    }

    public FieldDefinition setFinal(boolean value) {
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

    public FieldDefinition setStatic(boolean value) {
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

    public FieldDefinition setAccess(AccessTypeEnum access) {
        this.modifier = ModifierUtil.setAccess(modifier, access.getValue());
        return this;
    }

    public String getAccessTypeName() {
        return ModifierUtil.getAccessName(modifier);
    }

    public String getName() {
        return name;
    }

    public FieldDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public JavaType getType() {
        return type;
    }

    public FieldDefinition setType(JavaType type) {
        this.type = type;
        return this;
    }

    public String getInitExpression() {
        return initExpression;
    }

    public FieldDefinition setInitExpression(String initExpression) {
        this.initExpression = initExpression;
        return this;
    }

    public List<AnnotationDefinition> getAnnotationList() {
        return annotationList;
    }

    public FieldDefinition addAnnotation(AnnotationDefinition annotationDefinition) {
        if (annotationList == null) {
            annotationList = new ArrayList<>();
        }
        annotationList.add(annotationDefinition);
        return this;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if (importPackageNames != null) {
            classNames.addAll(importPackageNames);
        }
        if (type != null) {
            type.dealImport(classNames);
        }
        if (annotationList != null) {
            for (AnnotationDefinition annotationDefinition : annotationList) {
                annotationDefinition.dealImport(classNames);
            }
        }
    }
}
