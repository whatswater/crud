package com.whatswater.gen;

import java.util.Locale;
import java.util.Set;

/**
 * @author:heyajun
 * @Description:crud
 * @createTime:2021/11/12 17:41
 * @version:1.0
 */
public class JavaType implements AddImportClass {
    public static final JavaType OBJECT = new JavaType("java.lang.Object");
    public static final JavaType STRING = new JavaType("java.lang.String");
    public static final JavaType JSON_OBJECT = new JavaType("io.vertx.core.json.JsonObject");
    public static final JavaType VOID = new JavaType("void");

    // 是否是基本类型
    private boolean primitive;
    private PrimitiveType primitiveType;
    // 是否是数组类型
    private boolean array;
    // 类名
    private String className;
    // 是否是泛型名称T，E等
    private boolean genericName;
    // 泛型
    private JavaType[] generics;

    public JavaType(String className) {
        this.className = className;
        this.primitive = false;
        this.array = false;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public PrimitiveType getPrimitiveType() {
        return primitiveType;
    }

    public void setPrimitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
    }

    public boolean isArray() {
        return array;
    }

    public void setArray(boolean array) {
        this.array = array;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSimpleClassName() {
        if (primitive) {
            return primitiveType.name().toUpperCase(Locale.ROOT);
        }
        if (genericName) {
            return className;
        }
        return className.substring(className.lastIndexOf('.') + 1);
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if ((!primitive) && (!genericName) && className.contains(".")) {
            classNames.add(className);
        }
        if (generics != null) {
            for (JavaType javaType : generics) {
                javaType.dealImport(classNames);
            }
        }
    }

    public enum PrimitiveType {
        BYTE,
        SHORT,
        INT,
        LONG,
        BOOLEAN,
        CHAR,
        FLOAT,
        DOUBLE
    }
}
