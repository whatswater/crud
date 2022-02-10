package com.whatswater.gen;

import java.util.List;
import java.util.Set;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:41
 * @version:1.0
 */
public class ParameterDefinition implements AddImportClass {
    private JavaType javaType;
    private String name;
    private List<AnnotationDefinition> annotationList;

    public ParameterDefinition() {
    }

    public ParameterDefinition(String name) {
        this.name = name;
    }

    public ParameterDefinition(JavaType javaType, String name) {
        this.javaType = javaType;
        this.name = name;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public ParameterDefinition setJavaType(JavaType javaType) {
        this.javaType = javaType;
        return this;
    }

    public String getName() {
        return name;
    }

    public ParameterDefinition setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if (javaType != null) {
            javaType.dealImport(classNames);
        }
        if (annotationList != null) {
            for (AnnotationDefinition annotationDefinition : annotationList) {
                annotationDefinition.dealImport(classNames);
            }
        }
    }
}
