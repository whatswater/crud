package com.whatswater.curd.project.module.workflow.assignee;


import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.BusinessException;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.module.workflow.FlowConst;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstance;
import com.whatswater.curd.project.module.workflow.flowInstance.FlowInstanceService;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithConstant;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employee.EmployeeService;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.whatswater.curd.project.sys.permission.AuthService;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 获取任务参与者服务
 */
public class AssigneeConfigService {
    private WebClient workflowWebClient;
    private FlowInstanceService flowInstanceService;
    private AuthService authService;
    private EmployeeService employeeService;
    private OrganizationService organizationService;

    /**
     * 获取任务参与者
     * @param flowInstance 流程实例
     * @param flowLink 流程环节
     * @return 参与者列表
     */
    public Future<Tuple2<AssigneeConfigTypeEnum, Set<String>>> getAssignee(FlowInstance flowInstance, FlowLinkWithConstant flowLink) {
        String value = flowLink.getConfigValue(FlowConst.LINK_CONSTANT_PREFIX_ASSIGNEE);
        if (StrUtil.isEmpty(value)) {
            return Future.failedFuture("此环节未配置参与者：" + flowLink.getFlowDefinitionCode() + "." + flowLink.getFlowLinkCode());
        }
        AssigneeConfig assigneeConfig = CrudUtils.readValue(value, AssigneeConfig.class);
        AssigneeConfigTypeEnum typeEnum = AssigneeConfigTypeEnum.getByValue(assigneeConfig.getType());
        if (typeEnum == null) {
            return Future.failedFuture("不支持的参与者类型：" + assigneeConfig.getType());
        }
        switch (typeEnum) {
            case NONE:
            case USER_SELECT:
                return CrudUtils.successFuture(typeEnum, Collections.emptySet());
            case FLOW_VARIABLE:
            {
                String variableName = assigneeConfig.getConfig();
                String assigneeStr = flowInstance.getVariableValue(variableName);
                if (StrUtil.isEmpty(assigneeStr)) {
                    return Future.failedFuture("获取流程变量值为空，变量名：" + variableName + ", 值：" + assigneeStr);
                }
                return CrudUtils.successFuture(typeEnum, Arrays.stream(assigneeStr.split(StrUtil.COMMA)).filter(StrUtil::isNotEmpty).collect(Collectors.toSet()));
            }
            case ASSIGNEE:
            {
                String assigneeStr = assigneeConfig.getConfig();
                return CrudUtils.successFuture(typeEnum, Arrays.stream(assigneeStr.split(StrUtil.COMMA)).filter(StrUtil::isNotEmpty).collect(Collectors.toSet()));
            }
            case FILTER_SCRIPT:
            {
                String filterCode = assigneeConfig.getConfig();
                return authService.queryEmployeeByFilterCode(variableName -> {
                    if (FlowConst.FILTER_VARIABLE_NAME_CURRENT_TOP_ORG.equals(variableName)) {
                        String sender = flowInstance.getVariableValue(FlowConst.FLOW_VARIABLE_SENDER);
                        return employeeService.getByLoginName(sender).compose(employee -> {
                            if (employee == null) {
                                return Future.succeededFuture(StrUtil.EMPTY);
                            }
                            return organizationService.getTopLevelOrganization(employee.getOrganizationId()).map(org -> {
                               if (org == null) {
                                   return StrUtil.EMPTY;
                               }
                               return org.getOrganizationCode();
                            });
                        });
                    }
                    return Future.succeededFuture(StrUtil.EMPTY);
                }, filterCode).map(employees -> {
                    return Tuple2.of(typeEnum, employees.stream().map(Employee::getLoginName).collect(Collectors.toSet()));
                });
            }
            case HTTP:
            {
                String url = assigneeConfig.getConfig();
                return workflowWebClient
                    .post(url)
                    .putHeader(CrudConst.HEADER_TOKEN, CrudConst.WORKFLOW_TOKEN)
                    .sendJson(Buffer.buffer(CrudUtils.toJson(flowLink)))
                    .map(response -> {
                        JsonObject json = response.bodyAsJsonObject();
                        RestResult<String> result = RestResult.stringFromJsonObject(json);
                        if (!result.isSuccess()) {
                            throw new BusinessException("调用获取参与者接口失败, 错误码: " + result.getCode() + ", 错误信息: " + result.getMsg());
                        }
                        return Tuple2.of(typeEnum, Arrays.stream(result.getData().split(StrUtil.COMMA))
                            .filter(StrUtil::isNotEmpty)
                            .collect(Collectors.toSet()));
                    });
            }
            default:
                return Future.failedFuture("未知的参与者类型：" + assigneeConfig.getType());
        }
    }

    public void setWorkflowWebClient(WebClient workflowWebClient) {
        this.workflowWebClient = workflowWebClient;
    }

    public void setFlowInstanceService(FlowInstanceService flowInstanceService) {
        this.flowInstanceService = flowInstanceService;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}
