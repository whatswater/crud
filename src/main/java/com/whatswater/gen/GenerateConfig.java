package com.whatswater.gen;


public class GenerateConfig {
    private String templateFileName;
    private String generateFileName;

    public GenerateConfig(String templateFileName, ModuleConfig moduleConfig) {
        this.templateFileName = templateFileName;
        this.generateFileName = this.templateFileName.replaceAll("Entity", moduleConfig.getEntityName());
    }

    public GenerateConfig(String templateFileName, String generateFileName) {
        this.templateFileName = templateFileName;
        this.generateFileName = generateFileName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getGenerateFileName() {
        return generateFileName;
    }

    public void setGenerateFileName(String generateFileName) {
        this.generateFileName = generateFileName;
    }
}
