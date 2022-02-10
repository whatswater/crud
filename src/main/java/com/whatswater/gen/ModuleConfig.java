package com.whatswater.gen;

import cn.hutool.core.util.StrUtil;
import com.whatswater.gen.AnnotationDefinition.AnnotationValueDefinition;

import java.util.*;

public class ModuleConfig {
    private String tableName;
    private String packageName;
    private String entityName;
    private String captureEntityName;
    private String routePath;
    private String comment;
    private String primaryKey;
    private String[] mainProperty;
    private List<ColumnConfig> columnDefinitionList;
    private List<SearchOption> searchPropertyList;

    public ModuleConfig(String tableName) {
        this.tableName = tableName;
        this.columnDefinitionList = new ArrayList<>();
    }

    public ModuleConfig setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public static class ColumnConfig {
        private String fieldName;
        private String columnName;
        private String upperColumnName;
        private String type;
        private String comment;
        private String javaType;
        private boolean primary;

        public ColumnConfig() {
        }

        public ColumnConfig(String fieldName, String comment, String javaType) {
            this.fieldName = fieldName;
            this.columnName = StrUtil.toUnderlineCase(fieldName);
            this.upperColumnName = this.columnName.toUpperCase(Locale.ROOT);
            this.comment = comment;
            this.javaType = javaType;
        }

        public ColumnConfig(String fieldName, String columnName, String upperColumnName, String type, String comment, String javaType) {
            this.fieldName = fieldName;
            this.columnName = columnName;
            this.upperColumnName = upperColumnName;
            this.type = type;
            this.comment = comment;
            this.javaType = javaType;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getUpperColumnName() {
            return upperColumnName;
        }

        public void setUpperColumnName(String upperColumnName) {
            this.upperColumnName = upperColumnName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }

        public boolean isPrimary() {
            return "id".equals(fieldName);
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isNotPrimary() {
            return !"id".equals(fieldName);
        }
    }

    enum SearchType {
        EQ("andEq"),
        LIKE("andLike"),
        GE("andGte"),
        LE("andLte");

        private String method;
        SearchType(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }

    public static class SearchOption {
        private String name;
        private String fieldName;
        private SearchType searchType;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public SearchType getSearchType() {
            return searchType;
        }

        public void setSearchType(SearchType searchType) {
            this.searchType = searchType;
        }
    }

    private static String sqlType2JavaType(String t) {
        if (t.contains("char")) {
            return "String";
        } else if (t.contains("bigint")) {
            return "Long";
        } else if (t.contains("tinyint(1)")) {
            return "Boolean";
        } else if (t.contains("int")) {
            return "Integer";
        } else if (t.contains("text")) {
            return "String";
        } else if (t.contains("bit")) {
            return "Boolean";
        } else if (t.contains("decimal")) {
            return "BigDecimal";
            // "java.math.BigDecimal"
        } else if (t.contains("clob")) {
            return "Clob";
            // "java.sql.Clob";
        } else if (t.contains("blob")) {
            return "Blob";
            // "java.sql.Blob";
        } else if (t.contains("binary")) {
            return "byte[]";
        } else if (t.contains("float")) {
            return "float";
        } else if (t.contains("double")) {
            return "double";
        } else if (!t.contains("json") && !t.contains("enum")) {
            if (t.contains("time")) {
                return "LocalDateTime";
                // "java.time.LocalDateTime
            } else if (t.contains("date")) {
                return "LocalDate";
                // "java.time.LocalDate";
            } else {
                return "String";
            }
        } else {
            return "String";
        }
    }

    public ModuleConfig addSearchProperty(List<String> searchPropertyList) {
        for (String name: searchPropertyList) {
            addSearchProperty(name);
        }
        return this;
    }

    public ModuleConfig addSearchProperty(String name) {
        if (name.endsWith("Start")) {
            String fieldName = name.substring(0, name.length() - 5);
            boolean flag = false;
            for (ColumnConfig columnConfig: columnDefinitionList) {
                if (columnConfig.getFieldName().equals(fieldName)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                return addSearchProperty(name, fieldName, SearchType.GE);
            } else {
                return addSearchProperty(name, name, SearchType.EQ);
            }
        }

        if (name.endsWith("End")) {
            String fieldName = name.substring(0, name.length() - 3);
            boolean flag = false;
            for (ColumnConfig columnConfig: columnDefinitionList) {
                if (columnConfig.getFieldName().equals(fieldName)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                return addSearchProperty(name, fieldName, SearchType.LE);
            } else {
                return addSearchProperty(name, name, SearchType.EQ);
            }
        }

        return addSearchProperty(name, name, SearchType.EQ);
    }

    public ModuleConfig addSearchProperty(String name, SearchType searchType) {
        return addSearchProperty(name, name, searchType);
    }


    public ModuleConfig addSearchProperty(String name, String fileName, SearchType searchType) {
        SearchOption searchOption = new SearchOption();
        searchOption.fieldName = fileName;
        searchOption.searchType = searchType;
        searchOption.name = name;

        if (this.searchPropertyList == null) {
            this.searchPropertyList = new ArrayList<>();
        }
        this.searchPropertyList.add(searchOption);
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
        this.captureEntityName = entityName.substring(0, 1).toLowerCase(Locale.ROOT) + entityName.substring(1);
    }

    public String getCaptureEntityName() {
        return captureEntityName;
    }

    public String getComment() {
        return comment;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String[] getMainProperty() {
        return mainProperty;
    }

    public void setMainProperty(String[] mainProperty) {
        this.mainProperty = mainProperty;
    }

    public List<ColumnConfig> getColumnDefinitionList() {
        return columnDefinitionList;
    }

    public void setColumnDefinitionList(List<ColumnConfig> columnDefinitionList) {
        this.columnDefinitionList = columnDefinitionList;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }


    public ClassDefinition toEntityClass() {
        ClassDefinition classDefinition = new ClassDefinition(packageName, entityName);
        classDefinition
            .setComment(comment)
            .setAccess(AccessTypeEnum.PUBLIC);

        for (ColumnConfig columnConfig: columnDefinitionList) {
            classDefinition.addStaticFields(
                new FieldDefinition("COLUMN_" + columnConfig.getColumnName().toUpperCase(Locale.ROOT))
                    .setStatic(true)
                    .setFinal(true)
                    .setType(JavaType.STRING)
                    .setAccess(AccessTypeEnum.PUBLIC)
                    .setInitExpression("\"" + columnConfig.getColumnName() + "\"")
            );
        }
        classDefinition.addAnnotation(tableAnnotation(this.tableName));

        for (ColumnConfig columnConfig: columnDefinitionList) {
            String type = columnConfig.getJavaType();
            AnnotationDefinition tableColumn;
            if (columnConfig.getFieldName().equals(primaryKey)) {
                tableColumn = tableIdAnnotation("COLUMN_" + columnConfig.getColumnName().toUpperCase(Locale.ROOT));
            } else {
                tableColumn = tableColumnAnnotation("COLUMN_" + columnConfig.getColumnName().toUpperCase(Locale.ROOT));
            }

            classDefinition.addField(
                new FieldDefinition(columnConfig.fieldName)
                    .setStatic(false)
                    .setFinal(false)
                    .setType(simpleName2JavaType(type))
                    .setAccess(AccessTypeEnum.PRIVATE)
                    .addAnnotation(tableColumn)
            );
        }
        classDefinition.generateGetterSetter();
        classDefinition.addEmptyConstructor();

        ConstructorDefinition jsonConstructor = new ConstructorDefinition();
        StringBuilder code = new StringBuilder();
        for (ColumnConfig columnConfig: columnDefinitionList) {
            if ("LocalDateTime".equals(columnConfig.getJavaType())) {
                code.append("String ")
                    .append(columnConfig.getFieldName())
                    .append(" = json.getString(")
                    .append("COLUMN_").append(columnConfig.getColumnName().toUpperCase(Locale.ROOT))
                    .append(");\n")
                    .append("this.").append(columnConfig.getFieldName()).append(" = ")
                    .append(columnConfig.getFieldName()).append(" == null ? null :")
                    .append(columnConfig.getFieldName()).append(".atZone(ZoneId.systemDefault()).toLocalDateTime();\n");
                jsonConstructor.addCodeImportClass("java.time.Instant", "java.time.ZoneId", "java.time.LocalDateTime");
            } else if ("LocalDate".equals(columnConfig.getJavaType())) {
                code.append("String ")
                    .append(columnConfig.getFieldName())
                    .append(" = json.getString(")
                    .append("COLUMN_").append(columnConfig.getColumnName().toUpperCase(Locale.ROOT))
                    .append(");\n")
                    .append("this.").append(columnConfig.getFieldName()).append(" = ")
                    .append(columnConfig.getFieldName()).append(" == null ? null :")
                    .append(columnConfig.getFieldName()).append(".atZone(ZoneId.systemDefault()).toLocalDate();\n");
                jsonConstructor.addCodeImportClass("java.time.Instant", "java.time.ZoneId", "java.time.LocalDate");
            } else {
                code.append("this.").append(columnConfig.getFieldName())
                    .append(" = json.get").append(columnConfig.getJavaType())
                    .append("(").append("COLUMN_").append(columnConfig.getColumnName().toUpperCase(Locale.ROOT)).append(");\n");
            }
        }
        code.setLength(code.length() - 1);
        jsonConstructor
            .setAccess(AccessTypeEnum.PUBLIC)
            .addParameter(new ParameterDefinition("json").setJavaType(JavaType.JSON_OBJECT))
            .setCode(code.toString());
        classDefinition.addConstructor(jsonConstructor);
        return classDefinition;
    }

    public ClassDefinition toModuleClass() {
        ClassDefinition classDefinition = new ClassDefinition(packageName, entityName + "Module");
        classDefinition
            .setAccess(AccessTypeEnum.PUBLIC)
            .addImplementInterface(TYPE_MODULE_INTERFACE)
            .addField(createServiceField())
            .addField(createRouterField());

        MethodDefinition registerMethod = new MethodDefinition();
        registerMethod.addAnnotation(overrideAnnotation());
        registerMethod.setAccess(AccessTypeEnum.PUBLIC);
        registerMethod.setReturnType(JavaType.VOID);
        registerMethod.setName("register");
        registerMethod.addParameter(new ParameterDefinition().setJavaType(TYPE_MODULE_INFO).setName("moduleInfo"));
        String registerCode = "moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_DATA_SOURCE, \"datasource\");\n" +
            "moduleInfo.require(NewInstanceModuleFactory.MODULE_PATH_HTTP_SERVER, \"router\");";
        registerMethod.setCode(registerCode);
        registerMethod.addImportClass("com.whatswater.curd.NewInstanceModuleFactory");

        MethodDefinition onResolvedMethod = new MethodDefinition();
        onResolvedMethod
            .addAnnotation(overrideAnnotation())
            .setAccess(AccessTypeEnum.PUBLIC)
            .setReturnType(JavaType.VOID)
            .setName("onResolved")
            .addParameter(new ParameterDefinition().setJavaType(TYPE_MODULE_INFO).setName("consumer"))
            .addParameter(new ParameterDefinition().setJavaType(TYPE_MODULE_INFO).setName("provider"))
            .addParameter(new ParameterDefinition().setJavaType(JavaType.STRING).setName("name"))
            .addParameter(new ParameterDefinition().setJavaType(JavaType.OBJECT).setName("obj"))
            ;

        String onResolvedMethodCode = "if (\"datasource\".equals(name)) {\n" +
            "    MySQLPool pool = (MySQLPool) obj;\n" +
            "    {{captureEntityName}}Service = new {{entityName}}Service(pool);\n" +
            "    consumer.exportObject(\"{{captureEntityName}}Service\", {{captureEntityName}}Service);\n" +
            "} else if (\"router\".equals(name)) {\n" +
            "    router = (Router) obj;\n" +
            "}\n" +
            "\n" +

            "if (router != null && {{captureEntityName}}Service != null) {\n" +
            "    {{entityName}}Rest rest = new {{entityName}}Rest({{captureEntityName}}Service);\n" +
            "    RestRouter.register(router, rest);\n" +
            "}";

        onResolvedMethod.setCode(onResolvedMethodCode.replaceAll("\\{\\{captureEntityName}}", captureEntityName).replaceAll("\\{\\{entityName}}", entityName))
            .addImportClass("com.zandero.rest.RestRouter")
            .addImportClass("io.vertx.mysqlclient.MySQLPool");

        classDefinition.addMethod(registerMethod).addMethod(onResolvedMethod);
        return classDefinition;
    }

    public ClassDefinition toQueryClass() {
        ClassDefinition classDefinition = new ClassDefinition(packageName, entityName + "Query");
        classDefinition
            .setAccess(AccessTypeEnum.PUBLIC);

        for (SearchOption searchOption: searchPropertyList) {
            ColumnConfig config = findByFieldName(searchOption.fieldName);
            if (config == null) {
                continue;
            }

            FieldDefinition fieldDefinition = new FieldDefinition()
                .setAccess(AccessTypeEnum.PRIVATE)
                .setType(simpleName2JavaType(config.getJavaType()))
                .setName(searchOption.name);
            classDefinition.addField(fieldDefinition);
        }
        classDefinition.generateGetterSetter();

        MethodDefinition toSqlAssistMethod = new MethodDefinition();
        toSqlAssistMethod.setAccess(AccessTypeEnum.PUBLIC);
        toSqlAssistMethod.setReturnType(TYPE_SQL_ASSIST);
        toSqlAssistMethod.setName("toSqlAssist");
        StringBuilder code = new StringBuilder();

        code.append("SqlAssist sqlAssist = new SqlAssist();\n");
        for (SearchOption searchOption: searchPropertyList) {
            ColumnConfig config = findByFieldName(searchOption.fieldName);
            if (config == null) {
                continue;
            }

            if ("String".equals(config.getJavaType())) {
                String template = "if (StrUtil.isNotEmpty({{fieldName}})) {\n" +
                    "    sqlAssist.{{op}}({{columnRef}}, ";
                code.append(template
                    .replaceAll("\\{\\{fieldName}}", searchOption.getName())
                    .replaceAll("\\{\\{op}}", searchOption.searchType.getMethod())
                    .replaceAll("\\{\\{columnRef}}", entityName + ".COLUMN_" + config.getColumnName().toUpperCase(Locale.ROOT))
                );
                if (SearchType.LIKE.equals(searchOption.searchType)) {
                    code.append("\"%\" + ").append(config.getFieldName()).append(" + \"%\")").append(";\n");
                } else {
                    code.append(searchOption.getName()).append(");\n");
                }
                code.append("}\n");
                toSqlAssistMethod.addImportClass("cn.hutool.core.util.StrUtil");
            } else {
                String template = "if (Objects.nonNull({{fieldName}})) {\n" +
                    "    sqlAssist.{{op}}({{columnRef}}, ";
                code.append(template
                    .replaceAll("\\{\\{fieldName}}", searchOption.getName())
                    .replaceAll("\\{\\{op}}", searchOption.searchType.getMethod())
                    .replaceAll("\\{\\{columnRef}}", entityName + ".COLUMN_" + config.getColumnName().toUpperCase(Locale.ROOT))
                );
                code.append(searchOption.getName()).append(");\n");
                code.append("}\n");
                toSqlAssistMethod.addImportClass("java.util.Objects");
            }
        }
        code.append("return sqlAssist;");
        toSqlAssistMethod.setCode(code.toString());

        classDefinition.addMethod(toSqlAssistMethod);
        return classDefinition;
    }

    public ClassDefinition toRestClass() {
        FieldDefinition serviceField = createServiceField().setFinal(true);
        ClassDefinition classDefinition = new ClassDefinition(packageName, entityName + "Rest");
        classDefinition
            .setAccess(AccessTypeEnum.PUBLIC)
            .addField(serviceField);

        String code = "this.{{captureEntityName}}Service = {{captureEntityName}}Service;".replaceAll("\\{\\{captureEntityName}}", captureEntityName);
        ConstructorDefinition constructorDefinition = new ConstructorDefinition();
        constructorDefinition
            .setAccess(AccessTypeEnum.PUBLIC)
            .addParameter(createServiceParameter())
            .setCode(code);

        classDefinition.addConstructor(constructorDefinition);
        return classDefinition;
    }

    public List<ClassDefinition> toClassDefinitionList() {
        return Arrays.asList(toEntityClass(), toQueryClass(), toModuleClass());
    }

    static Map<String, String> map = new TreeMap<>();
    static {
        map.put("Long", "java.lang.Long");
        map.put("Integer", "java.lang.Integer");
        map.put("String", "java.lang.String");
        map.put("LocalDateTime", "java.time.LocalDateTime");
        map.put("LocalDate", "java.time.LocalDate");
    }

    private static JavaType simpleName2JavaType(String type) {
        String className = map.get(type);
        if (StrUtil.isEmpty(className)) {
            return new JavaType(type);
        }
        return new JavaType(className);
    }

    private static AnnotationDefinition tableAnnotation(String tableName) {
        String className = "io.vertx.ext.sql.assist.Table";
        return new AnnotationDefinition(className, "value", AnnotationValueDefinition.stringValue(tableName));
    }

    private static AnnotationDefinition tableColumnAnnotation(String tableColumnName) {
        String className = "io.vertx.ext.sql.assist.TableColumn";
        return new AnnotationDefinition(className, "value", AnnotationValueDefinition.value(tableColumnName));
    }

    private static AnnotationDefinition tableIdAnnotation(String tableColumnName) {
        String className = "io.vertx.ext.sql.assist.TableId";
        return new AnnotationDefinition(className, "value", AnnotationValueDefinition.value(tableColumnName));
    }

    private static AnnotationDefinition overrideAnnotation() {
        return new AnnotationDefinition("java.lang.Override");
    }

    private static List<AnnotationDefinition> requestBody(String path) {
        return Arrays.asList(
            new AnnotationDefinition("javax.ws.rs.POST"),
            new AnnotationDefinition("javax.ws.rs.Path", "value").setValue(AnnotationValueDefinition.stringValue(path)),
            new AnnotationDefinition("javax.ws.rs.Produces", "value").setValue(AnnotationValueDefinition.value("CrudConst.APPLICATION_JSON_UTF8")),
            new AnnotationDefinition("javax.ws.rs.Consumes", "value").setValue(AnnotationValueDefinition.value("MediaType.APPLICATION_JSON"))
        );
    }

    private static List<AnnotationDefinition> requestParam(String path) {
        return Arrays.asList(
            new AnnotationDefinition("javax.ws.rs.POST"),
            new AnnotationDefinition("javax.ws.rs.Path", "value").setValue(AnnotationValueDefinition.stringValue(path)),
            new AnnotationDefinition("javax.ws.rs.Produces", "value").setValue(AnnotationValueDefinition.value("CrudConst.APPLICATION_JSON_UTF8"))
        );
    }

    private static JavaType TYPE_MODULE_INTERFACE = new JavaType("com.whatswater.asyncmodule.Module");
    private static JavaType TYPE_MODULE_INFO = new JavaType("com.whatswater.asyncmodule.ModuleInfo");
    private static JavaType TYPE_SQL_ASSIST = new JavaType("io.vertx.ext.sql.assist.SqlAssist");
    private static JavaType TYPE_ROUTER = new JavaType("io.vertx.ext.web.Router");

    private FieldDefinition createServiceField() {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setAccess(AccessTypeEnum.FRIENDLY);
        fieldDefinition.setName(captureEntityName + "Service");
        JavaType type = new JavaType(packageName + "." + entityName + "Service");
        fieldDefinition.setType(type);

        return fieldDefinition;
    }

    private ParameterDefinition createServiceParameter() {
        ParameterDefinition parameterDefinition = new ParameterDefinition();
        JavaType type = new JavaType(packageName + "." + entityName + "Service");
        parameterDefinition.setJavaType(type);
        parameterDefinition.setName(entityName + "Service");
        return parameterDefinition;
    }

    private FieldDefinition createRouterField() {
        FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setAccess(AccessTypeEnum.FRIENDLY);
        fieldDefinition.setName("router");
        fieldDefinition.setType(TYPE_ROUTER);
        return fieldDefinition;
    }

    private ColumnConfig findByFieldName(String fieldName) {
        for (ColumnConfig config: columnDefinitionList) {
            if (fieldName.equals(config.getFieldName())) {
                return config;
            }
        }
        return null;
    }

}
