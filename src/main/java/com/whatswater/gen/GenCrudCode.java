package com.whatswater.gen;


import cn.hutool.core.io.FileUtil;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.gen.ModuleConfig.ColumnConfig;
import com.whatswater.gen.ModuleConfig.SearchType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// todo 修改版本设置；定义一个草稿版本
// 最终保存时生成一个新版本
public class GenCrudCode {
    public static ModuleConfig roleModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("sys_organization");
        moduleConfig.setEntityName("Organization");
        moduleConfig.setComment("组织机构表");
        moduleConfig.setPackageName("com.whatswater.curd.project.sys.organization");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setMainProperty(new String[] { "code" });
        moduleConfig.setRoutePath("/sys/organization");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "id", "ID", "", "", "Long"));
        columnConfigList.add(new ColumnConfig("path", "path", "PATH", "", "", "String"));
        columnConfigList.add(new ColumnConfig("level", "level", "LEVEL", "", "", "Integer"));
        columnConfigList.add(new ColumnConfig("parentId", "parent_id", "PARENT_ID", "", "", "Long"));
        columnConfigList.add(new ColumnConfig("leaf", "leaf", "LEAF", "", "", "Boolean"));

        columnConfigList.add(new ColumnConfig("organizationName", "organization_name", "ORGANIZATION_NAME", "", "", "String"));
        columnConfigList.add(new ColumnConfig("updateTraceId", "update_trace_id", "UPDATE_TRACE_ID", "", "", "Long"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        return moduleConfig;
    }

    public static ModuleConfig opinionModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("b_opinion");
        moduleConfig.setEntityName("Opinion");
        moduleConfig.setComment("流程意见表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.opinion");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/business/opinion");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowInstanceTaskId", "任务Id", "Long"));
        columnConfigList.add(new ColumnConfig("opinionApplicant", "意见提出人", "String"));
        columnConfigList.add(new ColumnConfig("content", "意见内容", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowInstanceTaskId", "opinionApplicant", "createTimeStart", "createTimeEnd");
        moduleConfig.addSearchProperty(searchList);
        return moduleConfig;
    }

    public static ModuleConfig attachmentModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("sys_attachment");
        moduleConfig.setEntityName("Attachment");
        moduleConfig.setComment("附件表");
        moduleConfig.setPackageName("com.whatswater.curd.project.sys.attachment");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/sys/attachment");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("businessType", "业务类型", "String"));
        columnConfigList.add(new ColumnConfig("businessId", "业务Id", "String"));
        columnConfigList.add(new ColumnConfig("fileOldName", "文件名", "String"));
        columnConfigList.add(new ColumnConfig("contentSize", "文件大小", "Long"));
        columnConfigList.add(new ColumnConfig("relativeFilePath", "文件路径", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("businessType", "businessId", "createTimeStart", "createTimeEnd");
        moduleConfig.addSearchProperty(searchList);
        moduleConfig.addSearchProperty("fileOldName", SearchType.LIKE);
        return moduleConfig;
    }

    public static ModuleConfig serialModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("sys_serial");
        moduleConfig.setEntityName("Serial");
        moduleConfig.setComment("序列号表");
        moduleConfig.setPackageName("com.whatswater.curd.project.sys.serial");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/sys/serial");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("topic", "主题", "String"));
        columnConfigList.add(new ColumnConfig("currentValue", "当前值", "Integer"));
        columnConfigList.add(new ColumnConfig("step", "步长", "Integer"));
        // 超过多少后查询下一个buffer
        columnConfigList.add(new ColumnConfig("filterValue", "阈值", "Integer"));
        columnConfigList.add(new ColumnConfig("expireDate", "过期日期", "LocalDate"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Collections.singletonList("topic");
        moduleConfig.addSearchProperty(searchList);
        return moduleConfig;
    }

    public static ModuleConfig employeeRoleModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("sys_employee_role");

        moduleConfig.setEntityName("EmployeeRole");
        moduleConfig.setComment("用户角色表");
        moduleConfig.setPackageName("com.whatswater.curd.project.sys.employeeRole");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/sys/employeeRole");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "", "Long"));
        columnConfigList.add(new ColumnConfig("roleId", "", "Long"));
        columnConfigList.add(new ColumnConfig("userId", "", "Long"));
        columnConfigList.add(new ColumnConfig("roleCode", "", "String"));
        columnConfigList.add(new ColumnConfig("userLoginName", "", "String"));

        moduleConfig.setColumnDefinitionList(columnConfigList);
        List<String> searchList = Arrays.asList("roleCode", "userLoginName");
        moduleConfig.addSearchProperty(searchList);
        return moduleConfig;
    }

    public static ModuleConfig employeeFilterModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("sys_employee_filter");

        moduleConfig.setEntityName("EmployeeFilter");
        moduleConfig.setComment("用户过滤器表");
        moduleConfig.setPackageName("com.whatswater.curd.project.sys.employeeFilter");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/sys/employeeFilter");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("code", "编码", "String"));
        columnConfigList.add(new ColumnConfig("path", "路径", "String"));
        columnConfigList.add(new ColumnConfig("value", "值", "String"));
        columnConfigList.add(new ColumnConfig("valueType", "值类型", "Integer"));

        moduleConfig.setColumnDefinitionList(columnConfigList);
        List<String> searchList = Arrays.asList("code", "valueType");
        moduleConfig.addSearchProperty(searchList);
        return moduleConfig;
    }

    public static ModuleConfig annualTaskModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("b_annual_task");
        moduleConfig.setEntityName("AnnualTask");
        moduleConfig.setComment("年度重点工作表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.annualTask");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/business/annualTask");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "", "Long"));
        columnConfigList.add(new ColumnConfig("year", "", "Integer"));
        columnConfigList.add(new ColumnConfig("content", "", "String"));
        columnConfigList.add(new ColumnConfig("parentId", "", "Long"));
        columnConfigList.add(new ColumnConfig("level", "", "Integer"));
        columnConfigList.add(new ColumnConfig("no", "序号", "Integer"));
        columnConfigList.add(new ColumnConfig("goal", "考核目标", "String"));
        columnConfigList.add(new ColumnConfig("sourceType", "来源", "Integer"));

        columnConfigList.add(new ColumnConfig("makeDate", "编制日志", "LocalDate"));
        columnConfigList.add(new ColumnConfig("versionNo", "版本号", "Integer"));
        columnConfigList.add(new ColumnConfig("status", "状态", "Integer"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("year", "content", "sourceType", "makeDate", "status");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    public static ModuleConfig flowDefinitionModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_definition");
        moduleConfig.setEntityName("FlowDefinition");
        moduleConfig.setComment("流程定义表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowDefinition");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowDefinition");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("title", "标题", "String"));
        columnConfigList.add(new ColumnConfig("remark", "备注", "String"));
        columnConfigList.add(new ColumnConfig("flowDefinitionCode", "唯一编码，树形结构", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));
        columnConfigList.add(new ColumnConfig("versionNo", "版本号", "Integer"));
        columnConfigList.add(new ColumnConfig("status", "状态", "Integer"));
        columnConfigList.add(new ColumnConfig("updateTraceId", "跟踪Id", "Long"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowDefinitionCode", "createTimeStart", "createTimeEnd");
        moduleConfig.addSearchProperty(searchList);
        moduleConfig.addSearchProperty("title", SearchType.LIKE);

        return moduleConfig;
    }

    public static ModuleConfig flowConstantModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_constant");
        moduleConfig.setEntityName("FlowConstant");
        moduleConfig.setComment("流程常量表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowConstant");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowConstant");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionId", "流程定义Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionCode", "流程定义编码", "String"));

        columnConfigList.add(new ColumnConfig("constantName", "常量名称", "String"));
        columnConfigList.add(new ColumnConfig("constantValue", "常量值", "String"));
        columnConfigList.add(new ColumnConfig("remark", "备注", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowDefinitionId", "flowDefinitionCode");
        moduleConfig.addSearchProperty(searchList);
        moduleConfig.addSearchProperty("remark", SearchType.LIKE);
        moduleConfig.addSearchProperty("constantName", SearchType.LIKE);

        return moduleConfig;
    }

    public static ModuleConfig flowLinkModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_link");
        moduleConfig.setEntityName("FlowLink");
        moduleConfig.setComment("流程环节表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowLink");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowLink");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionId", "流程定义Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionCode", "流程定义编码", "String"));

        columnConfigList.add(new ColumnConfig("flowLinkCode", "唯一编码", "String"));
        columnConfigList.add(new ColumnConfig("title", "标题", "String"));
        // link的类型可以是：正常、会签、串行节点、子流程节点、竞办等，可扩展
        columnConfigList.add(new ColumnConfig("type", "类型", "String"));
        columnConfigList.add(new ColumnConfig("remark", "备注", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowDefinitionId", "flowLinkCode", "flowDefinitionCode", "createTime");
        moduleConfig.addSearchProperty(searchList);
        moduleConfig.addSearchProperty("title", SearchType.LIKE);

        return moduleConfig;
    }

    public static ModuleConfig flowLinkRelationModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_link_relation");
        moduleConfig.setEntityName("FlowLinkRelation");
        moduleConfig.setComment("流程环节关联表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowLinkRelation");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowLinkRelation");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("startLinkId", "开始节点Id", "Long"));
        columnConfigList.add(new ColumnConfig("endLinkId", "结束节点Id", "Long"));
        // 当一个环节存在多个下一个环节时，通过routerName决定进行到哪一个环节，默认为default
        columnConfigList.add(new ColumnConfig("routerName", "路由名称", "String"));

        columnConfigList.add(new ColumnConfig("remark", "备注", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);
        List<String> searchList = Arrays.asList("startLinkId", "endLinkId", "routerName", "createTime");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    public static ModuleConfig flowLinkConstantModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_link_constant");
        moduleConfig.setEntityName("FlowLinkConstant");
        moduleConfig.setComment("流程环节表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowLinkConstant");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowLink");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowLinkId", "流程环节定义Id", "Long"));

        columnConfigList.add(new ColumnConfig("constantName", "常量名称", "String"));
        columnConfigList.add(new ColumnConfig("constantValue", "常量值", "String"));
        columnConfigList.add(new ColumnConfig("remark", "备注", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowLinkId", "createTime");
        moduleConfig.addSearchProperty(searchList);
        moduleConfig.addSearchProperty("remark", SearchType.LIKE);
        moduleConfig.addSearchProperty("constantName", SearchType.LIKE);

        return moduleConfig;
    }

    // 流程实例
    public static ModuleConfig flowInstanceModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_instance");
        moduleConfig.setEntityName("FlowInstance");
        moduleConfig.setComment("流程实例表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowInstance");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowInstance");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionId", "流程定义Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowDefinitionCode", "流程定义编码", "String"));
        columnConfigList.add(new ColumnConfig("flowVersionNo", "流程版本", "Integer"));

        columnConfigList.add(new ColumnConfig("startTime", "流程开始时间", "LocalDateTime"));
        columnConfigList.add(new ColumnConfig("startType", "开始方式", "Integer"));
        columnConfigList.add(new ColumnConfig("startUser", "开始人员", "String"));
        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowDefinitionId", "flowDefinitionCode", "startTime", "startType", "startUser");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    public static ModuleConfig flowInstanceVariableModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_instance_variable");
        moduleConfig.setEntityName("FlowInstanceVariable");
        moduleConfig.setComment("流程实例变量表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowInstanceVariable");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowInstanceVariable");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowInstanceId", "流程实例Id", "Long"));

        columnConfigList.add(new ColumnConfig("variableName", "变量名称", "String"));
        columnConfigList.add(new ColumnConfig("variableValue", "常量值", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("variableName", "flowInstanceId", "createTime");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    /**
     * 流程环节参与者表
     * @return 模块配置
     */
    public static ModuleConfig flowInstanceLinkActorModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_instance_link_actor");
        moduleConfig.setEntityName("FlowInstanceLinkActor");
        moduleConfig.setComment("流程实例环节参与者表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowInstanceLinkActor");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowInstanceLinkActor");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowInstanceId", "流程实例Id", "Long"));
        columnConfigList.add(new ColumnConfig("prevTaskId", "上一个任务Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowLinkId", "流程环节Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowLinkCode", "流程环节编码", "String"));
        columnConfigList.add(new ColumnConfig("actor", "任务参与者", "String"));
        columnConfigList.add(new ColumnConfig("actorName", "参与者名称", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowInstanceId", "flowLinkId", "flowLinkCode", "actor", "prevTaskId");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    public static ModuleConfig flowInstanceTaskModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_instance_task");
        moduleConfig.setEntityName("FlowInstanceTask");
        moduleConfig.setComment("流程实例任务表");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowInstanceTask");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowInstanceTask");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowInstanceId", "流程实例Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowLinkId", "流程环节Id", "Long"));
        columnConfigList.add(new ColumnConfig("flowLinkCode", "流程环节编码", "String"));

        columnConfigList.add(new ColumnConfig("type", "任务类型", "Integer"));
        columnConfigList.add(new ColumnConfig("status", "任务状态", "Integer"));
        columnConfigList.add(new ColumnConfig("actor", "任务参与者", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        List<String> searchList = Arrays.asList("flowInstanceId", "flowLinkId", "flowLinkCode", "actor", "status", "type", "createTime");
        moduleConfig.addSearchProperty(searchList);

        return moduleConfig;
    }

    public static ModuleConfig flowInstanceTaskRelationModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("w_flow_instance_task_relation");
        moduleConfig.setEntityName("FlowInstanceTaskRelation");
        moduleConfig.setComment("流程实例任务关联");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.workflow.flowInstanceTaskRelation");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/workflow/flowInstanceTaskRelation");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("prevTaskId", "上一个任务的Id", "Long"));
        columnConfigList.add(new ColumnConfig("nextTaskId", "下一个任务的Id", "Long"));

        moduleConfig.setColumnDefinitionList(columnConfigList);

        moduleConfig.addSearchProperty(Arrays.asList("prevTaskId", "nextTaskId"));
        return moduleConfig;
    }

    public static ModuleConfig projectRewardCategoryModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("b_project_reward_category");
        moduleConfig.setEntityName("ProjectRewardCategory");
        moduleConfig.setComment("专项激励类别");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.projectReward.projectRewardCategory");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/business/projectRewardCategory");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("year", "年份", "Integer"));
        columnConfigList.add(new ColumnConfig("category", "类别", "String"));
        columnConfigList.add(new ColumnConfig("totalCost", "总额", "BigDecimal"));
        columnConfigList.add(new ColumnConfig("level", "级别", "Integer"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);
        moduleConfig.addSearchProperty(Arrays.asList("year", "category", "level"));
        return moduleConfig;
    }

    public static ModuleConfig projectRewardItemModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("b_project_reward_item");
        moduleConfig.setEntityName("ProjectRewardItem");
        moduleConfig.setComment("专项激励子项目");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.projectReward.projectRewardItem");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/business/projectRewardItem");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("year", "年份", "Integer"));
        columnConfigList.add(new ColumnConfig("categoryId", "类别Id", "Long"));
        columnConfigList.add(new ColumnConfig("itemName", "子项目名称", "String"));
        columnConfigList.add(new ColumnConfig("standard", "标准", "String"));
        columnConfigList.add(new ColumnConfig("allocDepartmentIds", "分配部门Id列表", "String"));
        columnConfigList.add(new ColumnConfig("allocDepartmentNames", "分配部门Name列表", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));

        moduleConfig.setColumnDefinitionList(columnConfigList);
        moduleConfig.addSearchProperty(Arrays.asList("year", "categoryId", "itemName"));
        return moduleConfig;
    }

    public static ModuleConfig projectRewardApplyModuleConfig() {
        ModuleConfig moduleConfig = new ModuleConfig("b_project_reward_apply");
        moduleConfig.setEntityName("ProjectRewardApply");
        moduleConfig.setComment("专项激励申请单");
        moduleConfig.setPackageName("com.whatswater.curd.project.module.projectReward.projectRewardApply");
        moduleConfig.setPrimaryKey("id");
        moduleConfig.setRoutePath("/business/projectRewardApply");

        List<ColumnConfig> columnConfigList = new ArrayList<>();
        columnConfigList.add(new ColumnConfig("id", "主键Id", "Long"));
        columnConfigList.add(new ColumnConfig("title", "标题", "String"));

        columnConfigList.add(new ColumnConfig("year", "年份", "Integer"));
        columnConfigList.add(new ColumnConfig("categoryId", "类别Id", "Long"));
        columnConfigList.add(new ColumnConfig("category", "类别", "String"));
        columnConfigList.add(new ColumnConfig("contentId", "激励内容Id", "Long"));
        columnConfigList.add(new ColumnConfig("content", "激烈内容", "String"));
        columnConfigList.add(new ColumnConfig("itemId", "子项目Id", "Long"));
        columnConfigList.add(new ColumnConfig("item", "子项目", "String"));
        columnConfigList.add(new ColumnConfig("totalCost", "专项计划总额", "BigDecimal"));
        columnConfigList.add(new ColumnConfig("applyOrganizationCode", "申请部门编码", "String"));
        columnConfigList.add(new ColumnConfig("applyOrganizationName", "申请部门", "String"));
        columnConfigList.add(new ColumnConfig("allocOrganizationCode", "分配部门编码", "String"));
        columnConfigList.add(new ColumnConfig("allocOrganizationName", "分配部门", "String"));
        columnConfigList.add(new ColumnConfig("rewardStandard", "激励标准", "String"));

        columnConfigList.add(new ColumnConfig("systemBasis", "制度依据", "String"));
        columnConfigList.add(new ColumnConfig("applyContent", "申请内容", "String"));
        columnConfigList.add(new ColumnConfig("applyRemark", "申请备注", "String"));
        columnConfigList.add(new ColumnConfig("attachment", "附件Id", "Long"));
        columnConfigList.add(new ColumnConfig("applyMoney", "申请金额", "BigDecimal"));

        columnConfigList.add(new ColumnConfig("checkMoney", "审核金额", "BigDecimal"));
        columnConfigList.add(new ColumnConfig("checkRemark", "审核说明", "String"));

        columnConfigList.add(new ColumnConfig("applicantLoginName", "申请人", "String"));
        columnConfigList.add(new ColumnConfig("applicantName", "申请人姓名", "String"));
        columnConfigList.add(new ColumnConfig("createTime", "创建时间", "LocalDateTime"));
        columnConfigList.add(new ColumnConfig("firstCommitTime", "初次提交时间", "LocalDateTime"));
        columnConfigList.add(new ColumnConfig("status", "状态", "Integer"));


        moduleConfig.setColumnDefinitionList(columnConfigList);
        moduleConfig.addSearchProperty(Arrays.asList(
            "year", "categoryId", "itemId",
            "applicantLoginName", "title",
            "createTimeStart", "createTimeEnd", "firstCommitTimeStart", "firstCommitTimeEnd"));
        return moduleConfig;
    }

    public static void generateCode(File file, Handlebars handlebars, List<GenerateConfig> generateConfigs, ModuleConfig moduleConfig) throws IOException {
        boolean result = file.mkdirs();
        if (!result) {
            System.out.println("文件夹创建失败");
            return;
        }

        for (GenerateConfig generateConfig: generateConfigs) {
            Template template = handlebars.compile(generateConfig.getTemplateFileName());
            String code = template.apply(moduleConfig);

            String fileName = generateConfig.getGenerateFileName();
            FileUtil.appendString(code, new File(file, fileName + ".java"), StandardCharsets.UTF_8);
        }
    }

    public static void outCode(ModuleConfig moduleConfig) throws IOException {
        String folder = "D:\\code\\java\\crud\\src\\main\\java\\" + moduleConfig.getPackageName().replaceAll("\\.", "\\\\");
        File file = new File(folder);
        if (file.exists()) {
            System.out.println("文件夹已经存在");
            return;
        }

        TemplateLoader loader = new ClassPathTemplateLoader("/templates/crud");
        Handlebars handlebars = new Handlebars().with(loader);

        List<GenerateConfig> generateConfigs = new ArrayList<>();
        // generateConfigs.add(new GenerateConfig("Entity", moduleConfig));
        generateConfigs.add(new GenerateConfig("EntityRest", moduleConfig));
        // generateConfigs.add(new GenerateConfig("EntityModule", moduleConfig));
        generateConfigs.add(new GenerateConfig("EntityService", moduleConfig));
        generateConfigs.add(new GenerateConfig("EntitySQL", moduleConfig));
        // generateConfigs.add(new GenerateConfig("EntityQuery", moduleConfig));
        generateCode(file, handlebars, generateConfigs, moduleConfig);

        List<ClassDefinition> classDefinitions = moduleConfig.toClassDefinitionList();
        for (ClassDefinition classDefinition: classDefinitions) {
            String fileName = classDefinition.getClassName();
            FileUtil.appendString(classDefinition.toJavaCode(), new File(file, fileName + ".java"), StandardCharsets.UTF_8);
        }

        System.out.println("NewInstance:" + moduleConfig.getPackageName() + "." + moduleConfig.getEntityName() + "Module:0.1");
        String sql = generateSql(moduleConfig);
        System.out.println(sql);
    }

    public static void main(String[] args) throws IOException {
        List<ModuleConfig> moduleConfigList = Arrays.asList(
            flowInstanceLinkActorModuleConfig()
        );
        for (ModuleConfig moduleConfig: moduleConfigList) {
            outCode(moduleConfig);
        }
    }

    private static String getSqlType(String javaType) {
        if ("Long".equals(javaType)) {
            return "bigint(20)";
        } else if ("String".equals(javaType)) {
            return "varchar(50)";
        } else if ("LocalDateTime".equals(javaType)) {
            return "datetime";
        } else if ("LocalDate".equals(javaType)) {
            return "date";
        } else if ("Integer".equals(javaType)) {
            return "int(11)";
        } else if ("Short".equals(javaType)) {
            return "tinyint(4)";
        } else if ("Boolean".equals(javaType)) {
            return "tinyint(4)";
        }
        return "";
    }

    private static String getDefaultValue(String javaType) {
        if ("Long".equals(javaType)) {
            return "DEFAULT 0";
        } else if ("String".equals(javaType)) {
            return "DEFAULT ''";
        } else if ("LocalDateTime".equals(javaType)) {
            return "DEFAULT '1900-01-01 00:00:00'";
        } else if ("LocalDate".equals(javaType)) {
            return "DEFAULT '1900-01-01'";
        } else if ("Integer".equals(javaType)) {
            return "DEFAULT 0";
        } else if ("Short".equals(javaType)) {
            return "DEFAULT 0";
        } else if ("Boolean".equals(javaType)) {
            return "DEFAULT 0";
        }
        return "";
    }

    public static String generateSql(ModuleConfig moduleConfig) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE `").append(moduleConfig.getTableName()).append("` (\n");
        for (int i = 0; i < moduleConfig.getColumnDefinitionList().size(); i++) {
            ColumnConfig columnConfig = moduleConfig.getColumnDefinitionList().get(i);
            String javaType = columnConfig.getJavaType();
            String sqlType = getSqlType(javaType);

            sql.append("`").append(columnConfig.getColumnName()).append("` ").append(sqlType).append(" NOT NULL");
            if ("id".equals(columnConfig.getFieldName())) {
                sql.append(" AUTO_INCREMENT");
            } else {
                sql.append(" ").append(getDefaultValue(javaType));
            }
            sql.append(" COMMENT '").append(columnConfig.getComment()).append("',").append("\n");
        }
        sql.append("PRIMARY KEY (`id`)");
        sql.append(")");
        return sql.toString();
    }
}
