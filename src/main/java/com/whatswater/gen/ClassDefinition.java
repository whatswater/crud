package com.whatswater.gen;


import cn.hutool.core.util.StrUtil;
import com.whatswater.gen.AnnotationDefinition.AnnotationValueDefinition;

import java.util.*;
import java.util.stream.Collectors;

// todo 优化泛型，添加extends super等通配符
public class ClassDefinition implements AddImportClass {
    private int modifier;
    private String packageName;
    private SortedSet<String> importClassNames;
    private String className;
    private JavaType extendsClass;
    private List<JavaType> implementInterfaceList;
    private List<FieldDefinition> staticFields;
    private String staticCode;
    private List<AnnotationDefinition> annotationList;
    private String comment;
    private final List<FieldDefinition> fields;
    private final List<ConstructorDefinition> constructorList;
    private final List<MethodDefinition> methodList;

    public ClassDefinition() {
        this.extendsClass = JavaType.OBJECT;
        this.fields = new ArrayList<>();
        this.constructorList = new ArrayList<>();
        this.methodList = new ArrayList<>();
    }

    public ClassDefinition(String packageName, String className) {
        this();
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public ClassDefinition setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public SortedSet<String> getImportClassNames() {
        return importClassNames;
    }

    public ClassDefinition addImport(String className) {
        if (importClassNames == null) {
            importClassNames = new TreeSet<>();
        }

        importClassNames.add(className);
        return this;
    }

    public ClassDefinition setFinal(boolean value) {
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

    public ClassDefinition setAccess(AccessTypeEnum access) {
        this.modifier = ModifierUtil.setAccess(modifier, access.getValue());
        return this;
    }

    public String getAccessTypeName() {
        return ModifierUtil.getAccessName(modifier);
    }

    public String getClassName() {
        return className;
    }

    public ClassDefinition setClassName(String className) {
        this.className = className;
        return this;
    }

    public JavaType getExtendsClass() {
        return extendsClass;
    }

    public ClassDefinition setExtendsClass(JavaType extendsClass) {
        this.extendsClass = extendsClass;
        return this;
    }

    public List<JavaType> getImplementInterfaceList() {
        return implementInterfaceList;
    }

    public ClassDefinition addImplementInterface(JavaType implementInterface) {
        if (this.implementInterfaceList == null) {
            this.implementInterfaceList = new ArrayList<>();
        }
        this.implementInterfaceList.add(implementInterface);
        return this;
    }

    public List<FieldDefinition> getStaticFields() {
        return staticFields;
    }

    public ClassDefinition addStaticFields(FieldDefinition staticField) {
        if (this.staticFields == null) {
            this.staticFields = new ArrayList<>();
        }
        this.staticFields.add(staticField);
        return this;
    }

    public String getStaticCode() {
        return staticCode;
    }

    public void setStaticCode(String staticCode) {
        this.staticCode = staticCode;
    }

    public List<AnnotationDefinition> getAnnotationList() {
        return annotationList;
    }

    public ClassDefinition addAnnotation(AnnotationDefinition annotationDefinition) {
        if (annotationList == null) {
            this.annotationList = new ArrayList<>();
        }
        this.annotationList.add(annotationDefinition);
        return this;
    }

    public String getComment() {
        return comment;
    }

    public ClassDefinition setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public ClassDefinition addField(FieldDefinition field) {
        this.fields.add(field);
        return this;
    }

    public void generateGetterSetter() {
        for (FieldDefinition fieldDefinition: fields) {
            String getterName = "get" + StrUtil.upperFirst(fieldDefinition.getName());

            MethodDefinition getter = new MethodDefinition();
            getter
                .setStatic(false)
                .setAccess(AccessTypeEnum.PUBLIC)
                .setFinal(false)
                .setSynchronized(false)
                .setName(getterName)
                .setReturnType(fieldDefinition.getType())
                .setCode("return this." + fieldDefinition.getName() + ";");

            addMethod(getter);

            String setterName = "set" + StrUtil.upperFirst(fieldDefinition.getName());
            MethodDefinition setter = new MethodDefinition();
            setter
                .setStatic(false)
                .setAccess(AccessTypeEnum.PUBLIC)
                .setFinal(false)
                .setSynchronized(false)
                .setName(setterName)
                .setReturnType(JavaType.VOID)
                .addParameter(new ParameterDefinition().setJavaType(fieldDefinition.getType()).setName(fieldDefinition.getName()))
                .setCode("this." + fieldDefinition.getName() + " = " + fieldDefinition.getName() + ";");
            addMethod(setter);
        }
    }

    public List<MethodDefinition> getMethodList() {
        return this.methodList;
    }

    public ClassDefinition addMethod(MethodDefinition methodDefinition) {
        this.methodList.add(methodDefinition);
        return this;
    }

    public List<ConstructorDefinition> getConstructorList() {
        return this.constructorList;
    }

    public ClassDefinition addConstructor(ConstructorDefinition constructorDefinition) {
        this.constructorList.add(constructorDefinition);
        return this;
    }

    public ClassDefinition addEmptyConstructor() {
        ConstructorDefinition constructorDefinition = new ConstructorDefinition();
        constructorDefinition
            .setAccess(AccessTypeEnum.PUBLIC);
        this.constructorList.add(constructorDefinition);

        return this;
    }

    @Override
    public void dealImport(Set<String> classNames) {
        if (extendsClass != null) {
            extendsClass.dealImport(classNames);
        }
        if (implementInterfaceList != null) {
            for (JavaType face: implementInterfaceList) {
                face.dealImport(classNames);
            }
        }
        if (staticFields != null) {
            for (FieldDefinition fieldDefinition: staticFields) {
                fieldDefinition.dealImport(classNames);
            }
        }
        if (annotationList != null) {
            for (AnnotationDefinition annotationDefinition: annotationList) {
                annotationDefinition.dealImport(classNames);
            }
        }
        if (fields != null) {
            for (FieldDefinition fieldDefinition: fields) {
                fieldDefinition.dealImport(classNames);
            }
        }
        if (importClassNames != null) {
            classNames.addAll(importClassNames);
        }
        if (constructorList != null) {
            for (ConstructorDefinition constructorDefinition: constructorList) {
                constructorDefinition.dealImport(classNames);
            }
        }
        if (methodList != null) {
            for (MethodDefinition methodDefinition: methodList) {
                methodDefinition.dealImport(classNames);
            }
        }
    }

    public String toJavaCode() {
        Set<String> importClassNames = new TreeSet<>();
        this.dealImport(importClassNames);

        CodePrinter codePrinter = new CodePrinter();
        codePrinter.print("package ").print(packageName).print(";").newLine(2);

        if (!importClassNames.isEmpty()) {
            codePrinter.newLine();
            for (String className: importClassNames) {
                if (className.startsWith("java.lang")) {
                    continue;
                }

                codePrinter.print("import ").print(className).print(";").newLine();
            }
        }

        codePrinter.newLine();
        if (annotationList != null && (!annotationList.isEmpty())) {
            List<String> codeList = generateAnnotationCode(annotationList);
            for (String code: codeList) {
                codePrinter.print(code).newLine();
            }
        }
        codePrinter.print(getAccessTypeName()).print(" ");
        if (isFinal()) {
            codePrinter.print("final").print(" ");
        }
        codePrinter.print("class").print(" ").print(className);
        if (extendsClass != null && (!JavaType.OBJECT.equals(extendsClass))) {
            codePrinter.print(" extends ").print(extendsClass.getSimpleClassName());
        }
        if (implementInterfaceList != null && (!implementInterfaceList.isEmpty())) {
            codePrinter.print(" implements ");
            for (JavaType t: implementInterfaceList) {
                codePrinter.print(t.getSimpleClassName()).print(", ");
            }
            codePrinter.back(2);
        }
        codePrinter.print(" ").newBlock();
        if (staticFields != null) {
            for (FieldDefinition fieldDefinition: staticFields) {
                codePrinter.print("public static final");
                codePrinter.print(" ").print(fieldDefinition.getType().getSimpleClassName());
                codePrinter.print(" ").print(fieldDefinition.getName());
                codePrinter.print(" = ").print(fieldDefinition.getInitExpression()).print(";");
                codePrinter.newLine();
            }
        }
        if (StrUtil.isNotEmpty(staticCode)) {
            codePrinter.newLine();
            codePrinter.print("static ");
            codePrinter.newBlock();

            codePrinter.print(staticCode);
            codePrinter.exitBlock();
            codePrinter.newLine();
        }

        if (fields != null && (!fields.isEmpty())) {
            codePrinter.newLine();
            for (FieldDefinition fieldDefinition: fields) {
                List<AnnotationDefinition> annotationDefinitionList = fieldDefinition.getAnnotationList();
                if (annotationDefinitionList != null && (!annotationDefinitionList.isEmpty())) {
                    List<String> codeList = generateAnnotationCode(fieldDefinition.getAnnotationList());
                    for (String code: codeList) {
                        codePrinter.print(code).newLine();
                    }
                }

                codePrinter.print(fieldDefinition.getType().getSimpleClassName());
                codePrinter.print(" ").print(fieldDefinition.getName());
                if (StrUtil.isNotEmpty(fieldDefinition.getInitExpression())) {
                    codePrinter.print(" = ").print(fieldDefinition.getInitExpression());
                }
                codePrinter.print(";");
                codePrinter.newLine();
            }
        }

        if (constructorList != null && (!constructorList.isEmpty())) {
            for (ConstructorDefinition constructorDefinition: constructorList) {
                codePrinter.newLine(2);
                codePrinter
                    .print(constructorDefinition.getAccessName())
                    .print(" ").print(className).print("(");
                List<ParameterDefinition> parameterDefinitionList = constructorDefinition.getParameterList();
                if (parameterDefinitionList != null && (!parameterDefinitionList.isEmpty())) {
                    for (ParameterDefinition parameter: parameterDefinitionList) {
                        codePrinter
                            .print(parameter.getJavaType().getSimpleClassName())
                            .print(" ").print(parameter.getName()).print(", ");
                    }
                    codePrinter.back(2);
                }
                codePrinter.print(") ").newBlock();
                if (StrUtil.isNotEmpty(constructorDefinition.getCode())) {
                    codePrinter.print(constructorDefinition.getCode());
                }
                codePrinter.exitBlock();
            }
        }

        if (methodList != null && (!methodList.isEmpty())) {
            for (MethodDefinition methodDefinition: methodList) {
                codePrinter.newLine(2);

                List<AnnotationDefinition> annotationDefinitionList = methodDefinition.getAnnotationList();
                if (annotationDefinitionList != null && (!annotationDefinitionList.isEmpty())) {
                    List<String> codeList = generateAnnotationCode(methodDefinition.getAnnotationList());
                    for (String code: codeList) {
                        codePrinter.print(code).newLine();
                    }
                }

                codePrinter.print(methodDefinition.getAccessName());

                if (methodDefinition.isStatic()) {
                    codePrinter.print(" static");
                }
                if (methodDefinition.isFinal()) {
                    codePrinter.print(" final");
                }
                if (methodDefinition.isSynchronized()) {
                    codePrinter.print(" synchronized");
                }
                codePrinter.print(" ").print(methodDefinition.getReturnType().getSimpleClassName());

                // 处理泛型

                codePrinter.print(" ").print(methodDefinition.getName()).print("(");
                List<ParameterDefinition> parameterDefinitionList = methodDefinition.getParameterList();
                if (parameterDefinitionList != null && (!parameterDefinitionList.isEmpty())) {
                    for (ParameterDefinition parameter: parameterDefinitionList) {
                        codePrinter
                            .print(parameter.getJavaType().getSimpleClassName())
                            .print(" ").print(parameter.getName()).print(", ");
                        ;
                    }
                    codePrinter.back(2);
                }
                codePrinter.print(") ").newBlock();
                if (StrUtil.isNotEmpty(methodDefinition.getCode())) {
                    codePrinter.print(methodDefinition.getCode());
                }
                codePrinter.exitBlock();
            }
        }
        codePrinter.exitBlock();
        codePrinter.newLine();

        return codePrinter.getCode().toString();
    }


    public static List<String> generateAnnotationCode(List<AnnotationDefinition> annotationList) {
        Map<String, List<AnnotationDefinition>> annotationMap = annotationList.stream().collect(Collectors.groupingBy(AnnotationDefinition::getAnnotationName));

        List<String> codeList = new ArrayList<>();
        for (Map.Entry<String, List<AnnotationDefinition>> entry: annotationMap.entrySet()) {
            String annotationName = entry.getKey();
            List<AnnotationDefinition> propertyList = entry.getValue();

            StringBuilder code = new StringBuilder();
            code.append("@").append(annotationName);

            if (propertyList.size() == 1) {
                AnnotationDefinition property = propertyList.get(0);
                if (StrUtil.isNotEmpty(property.getPropertyName())) {
                    if ("value".equals(property.getPropertyName())) {
                        code.append("(").append(generateAnnotationValueCode(property)).append(")");
                    } else {
                        code.append("(").append(property.getPropertyName()).append(" = ").append(generateAnnotationValueCode(property)).append(")");
                    }
                }
            } else {
                for (AnnotationDefinition property: propertyList) {
                    code.append("(").append(property.getPropertyName()).append(" = ").append(generateAnnotationValueCode(property)).append(")");
                }
            }
            codeList.add(code.toString());
        }
        return codeList;
    }

    public static String generateAnnotationValueCode(AnnotationDefinition annotationDefinition) {
        StringBuilder code = new StringBuilder();

        boolean isArray = annotationDefinition.isArrayValue();
        if (isArray) {
            code.append("{");
            for (AnnotationValueDefinition value: annotationDefinition.getValueList()) {
                code.append(value.getPropertyValue()).append(", ");
            }
            code.setLength(code.length() - 2);
            code.append("}");
        } else {
            AnnotationValueDefinition value = annotationDefinition.getValue();
            code.append(value.getPropertyValue());
        }
        return code.toString();
    }

}
