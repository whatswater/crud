package com.whatswater.gen;

import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Set;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:42
 * @version:1.0
 */
public class AnnotationDefinition implements AddImportClass {
    private String annotationName;
    private String className;
    private String propertyName;
    private boolean arrayValue;
    private AnnotationValueDefinition value;
    private List<AnnotationValueDefinition> valueList;

    public AnnotationDefinition(String annotationName) {
        this.annotationName = annotationName.substring(annotationName.lastIndexOf(".") + 1);
        this.className = annotationName;
    }

    public AnnotationDefinition(String annotationName, String propertyName) {
        this(annotationName);
        this.propertyName = propertyName;
    }

    public AnnotationDefinition(String annotationName, String propertyName, AnnotationValueDefinition value) {
        this(annotationName, propertyName);
        this.value = value;
        this.arrayValue = false;
    }

    public AnnotationDefinition(String annotationName, String propertyName, List<AnnotationValueDefinition> value) {
        this(annotationName, propertyName);
        this.arrayValue = true;
        this.valueList = value;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean isArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(boolean arrayValue) {
        this.arrayValue = arrayValue;
    }

    public AnnotationValueDefinition getValue() {
        return value;
    }

    public AnnotationDefinition setValue(AnnotationValueDefinition value) {
        this.value = value;
        return this;
    }

    public List<AnnotationValueDefinition> getValueList() {
        return valueList;
    }

    public void setValueList(List<AnnotationValueDefinition> valueList) {
        this.valueList = valueList;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        classNames.add(className);
        if (arrayValue && valueList != null) {
            for (AnnotationValueDefinition valueDefinition : valueList) {
                valueDefinition.dealImport(classNames);
            }
        }
        if (!arrayValue && value != null) {
            value.dealImport(classNames);
        }
    }

    /**
     * @author:heyajun
     * @Description:crud
     * @createTime:2021/11/12 17:42
     * @version:1.0
     */
    public static class AnnotationValueDefinition implements AddImportClass {
        private final String importClassName;
        private final String propertyValue;
        // 值是否是字符串字面量
        private final boolean stringLiteral;
        private final boolean classLiteral;

        public AnnotationValueDefinition(String propertyValue, boolean stringLiteral, boolean classLiteral) {
            if (classLiteral) {
                int idx = propertyValue.lastIndexOf('.');
                this.propertyValue = propertyValue.substring(idx + 1) + ".class";
                this.importClassName = propertyValue.substring(0, idx);
            }
            else if (stringLiteral) {
                this.propertyValue = "\"" + propertyValue + "\"";
                this.importClassName = StrUtil.EMPTY;
            }
            else {
                this.propertyValue = propertyValue;
                this.importClassName = StrUtil.EMPTY;
            }
            this.stringLiteral = stringLiteral;
            this.classLiteral = classLiteral;
        }

        public String getPropertyValue() {
            return propertyValue;
        }

        public boolean isStringLiteral() {
            return stringLiteral;
        }

        public boolean isClassLiteral() {
            return classLiteral;
        }

        public static AnnotationValueDefinition value(String value) {
            return new AnnotationValueDefinition(value, false, false);
        }

        public static AnnotationValueDefinition stringValue(String value) {
            return new AnnotationValueDefinition(value, true, false);
        }

        public static AnnotationValueDefinition classValue(String className) {
            return new AnnotationValueDefinition(className, false, true);
        }

        @Override
        public void dealImport(Set<String> classNames) {
            if (StrUtil.isNotEmpty(importClassName)) {
                classNames.add(importClassName);
            }
        }
    }
}
