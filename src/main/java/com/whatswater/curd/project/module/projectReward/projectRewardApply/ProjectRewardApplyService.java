package com.whatswater.curd.project.module.projectReward.projectRewardApply;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.whatswater.curd.project.common.CrudUtils;
import com.whatswater.curd.project.common.CrudUtils.SameFutureBuilder;
import com.whatswater.curd.project.common.CrudUtils.Tuple2;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.FlowConst;
import com.whatswater.curd.project.module.workflow.assignee.AssigneeConfigTypeEnum;
import com.whatswater.curd.project.module.workflow.flowDefinition.FlowStartTypeEnum;
import com.whatswater.curd.project.module.workflow.flowEngine.FlowEngineService;
import com.whatswater.curd.project.module.workflow.flowEngine.context.ContextFlowInstanceStart;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithCandidates;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithCandidates.Candidate;
import com.whatswater.curd.project.sys.attachment.AttachmentService;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.employeeRole.EmployeeRoleService;
import com.whatswater.curd.project.sys.organization.OrganizationService;
import com.whatswater.curd.project.sys.permission.UserToken;
import com.whatswater.curd.project.sys.permission.UserTokenService;
import com.whatswater.curd.project.sys.serial.SerialService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.assist.SqlAssist;
import io.vertx.mysqlclient.MySQLClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectRewardApplyService {
    private ProjectRewardApplySQL projectRewardApplySQL;
    private OrganizationService organizationService;
    private UserTokenService userTokenService;
    private FlowEngineService flowEngineService;
    private SerialService serialService;
    private AttachmentService attachmentService;
    private EmployeeRoleService employeeRoleService;

    public ProjectRewardApplyService() {
    }

    public Future<PageResult<ProjectRewardApply>> search(Page page, ProjectRewardApplyQuery query) {
        SqlAssist sqlAssist = query.toSqlAssist();
        sqlAssist.setStartRow(page.getOffset());
        sqlAssist.setRowSize(page.getLimit());

        return projectRewardApplySQL.getCount(sqlAssist).compose(total -> {
            if (CrudUtils.notZero(total)) {
                return projectRewardApplySQL.selectAll(sqlAssist).map(list -> PageResult.of(list.stream().map(ProjectRewardApply::new).collect(Collectors.toList()), page, total));
            } else {
                return Future.succeededFuture(PageResult.empty());
            }
        });
    }

    public Future<ProjectRewardApply> initApply(Long applyId) {
        return getById(applyId);
    }

    public Future<ProjectRewardApply> initApply(String token) {
        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            return Future.failedFuture("当前用户未登录，请重新登录");
        }
        return initApply(userToken.getEmployee());
    }

    public Future<ProjectRewardApply> initApply(Employee employee) {
        Long organizationId = employee.getOrganizationId();
        final LocalDateTime now = LocalDateTime.now();

        return organizationService.getTopLevelOrganization(organizationId).compose(organization -> {
            String key = "PROJECT_REWARD_" + CrudUtils.formatDate(LocalDate.now());
            return serialService.nextSerial(key).map(serialNo -> {
                ProjectRewardApply projectRewardApply = new ProjectRewardApply();
                projectRewardApply.setApplyNo(key + StrUtil.padPre(String.valueOf(serialNo), 4, '0'));

                projectRewardApply.setApplyOrganizationCode(organization.getOrganizationCode());
                projectRewardApply.setApplyOrganizationName(organization.getOrganizationName());

                projectRewardApply.setYear(now.getYear());
                projectRewardApply.setTitle(projectRewardApply.getYear() + "年度各类专项激励申请/审批单");
                return projectRewardApply;
            });
        });
    }

    public Future<ProjectRewardApply> getById(Long projectRewardApplyId) {
        Future<JsonObject> result = projectRewardApplySQL.selectById(projectRewardApplyId);
        return result.map(ProjectRewardApplyService::mapOne);
    }

    public Future<ProjectRewardApply> getByApplyNo(String applyNo) {
        SqlAssist sqlAssist = ProjectRewardApply.applyNoSqlAssist(applyNo);
        return projectRewardApplySQL.selectAll(sqlAssist).map(ProjectRewardApplyService::mapOne);
    }

    private static ProjectRewardApply mapOne(JsonObject json) {
        if (json == null) {
            return null;
        }
        return new ProjectRewardApply(json);
    }

    private static ProjectRewardApply mapOne(List<JsonObject> json) {
        if (CollectionUtil.isEmpty(json)) {
            return null;
        }
        return new ProjectRewardApply(json.get(0));
    }

    public Future<Long> fillAndInsertWithCheck(String token, ProjectRewardApply projectRewardApply) {
        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            return Future.failedFuture("当前用户未登录，请重新登录");
        }
        Employee employee = userToken.getEmployee();

        String applyNo = projectRewardApply.getApplyNo();
        if (StrUtil.isEmpty(applyNo)) {
            return Future.failedFuture("申请单号不能为空");
        }

        return getByApplyNo(applyNo).compose(old -> {
            if (old != null) {
                return Future.failedFuture("系统中已经存在相同单号的申请单：" + applyNo);
            }
            projectRewardApply.setApplicantLoginName(employee.getLoginName());
            projectRewardApply.setApplicantName(employee.getName());
            projectRewardApply.setCreateTime(LocalDateTime.now());
            projectRewardApply.setStatus(ProjectRewardApplyStatusEnum.INIT.getCode());
            return insert(projectRewardApply).compose(id -> {
                String attachment = projectRewardApply.getAttachment();
                if (StrUtil.isEmpty(attachment)) {
                    return Future.succeededFuture(id);
                }
                List<Long> attachmentIds = Arrays.stream(attachment.split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList());
                return attachmentService
                    .updateBusinessTypeAndId(attachmentIds, "PROJECT_REWARD_APPLY", String.valueOf(id))
                    .map(id);
            });
        });
    }

    public Future<Integer> updateWithCheck(ProjectRewardApply apply) {
        return getById(apply.getId()).compose(old -> {
            if (old == null) {
                return Future.failedFuture("更新申请单失败，系统中已不存在此申请单");
            }

            ProjectRewardApply updateData = new ProjectRewardApply();
            updateData.setAllocOrganizationCode(apply.getAllocOrganizationCode());
            updateData.setAllocOrganizationName(apply.getAllocOrganizationName());
            updateData.setApplyContent(apply.getApplyContent());
            updateData.setApplyMoney(apply.getApplyMoney());
            updateData.setAttachment(apply.getAttachment());
            updateData.setCategory(apply.getCategory());
            updateData.setCategoryId(apply.getCategoryId());
            updateData.setCheckMoney(apply.getCheckMoney());
            updateData.setCheckRemark(apply.getCheckRemark());
            updateData.setContent(apply.getContent());
            updateData.setApplyRemark(apply.getApplyRemark());
            updateData.setContentId(apply.getContentId());
            updateData.setItem(apply.getItem());
            updateData.setItemId(apply.getItemId());
            updateData.setRewardStandard(apply.getRewardStandard());
            updateData.setSystemBasis(apply.getSystemBasis());
            updateData.setCategoryTotalCost(apply.getCategoryTotalCost());
            updateData.setContentTotalCost(apply.getContentTotalCost());

            String businessType = "PROJECT_REWARD_APPLY";
            String businessId = String.valueOf(apply.getId());
            return update(updateData).compose(cnt -> attachmentService.deleteBy(businessType, businessId).map(cnt)).compose(cnt -> {
                String attachment = apply.getAttachment();
                if (StrUtil.isEmpty(attachment)) {
                    return Future.succeededFuture();
                }
                List<Long> attachmentIds = Arrays.stream(attachment.split(StrUtil.COMMA)).map(Long::parseLong).collect(Collectors.toList());
                return attachmentService
                    .updateBusinessTypeAndId(attachmentIds, businessType, businessId)
                    .map(cnt);
            });
        });
    }

    public Future<Long> insert(ProjectRewardApply projectRewardApply) {
        return projectRewardApplySQL.insertNonEmptyGeneratedKeys(projectRewardApply, MySQLClient.LAST_INSERTED_ID);
    }

    private SameFutureBuilder<FlowLinkWithCandidates> taskOfQueryCandidates(FlowLink flowLink, FlowInstanceTask task) {
        return ignored -> flowEngineService.getFlowLinkService().withConstant(flowLink).compose(flowLinkWithConstant -> {
            return flowEngineService.getFlowInstanceService()
                .getById(task.getFlowInstanceId())
                .compose(flowInstance -> flowEngineService.getFlowInstanceVariableService().withVariable(flowInstance))
                .map(flowInstance -> Tuple2.of(flowInstance, flowLinkWithConstant));
        }).compose(tuple2 -> {
            return flowEngineService.getAssigneeConfigService()
                .getAssignee(tuple2._1, tuple2._2)
                .compose(r -> {
                    FlowLinkWithCandidates flowLinkWithCandidates = FlowLinkWithCandidates.fromFlowLink(flowLink);
                    if (AssigneeConfigTypeEnum.USER_SELECT.equals(r._1)) {
                        return employeeRoleService.listEmployeeOfRoleCode("department_manager")
                            .map(employees -> employees.stream().map(Candidate::fromEmployee).collect(Collectors.toList()))
                            .map(candidates -> {
                                flowLinkWithCandidates.setCandidates(candidates);
                                return flowLinkWithCandidates;
                            });
                    }
                    flowLinkWithCandidates.setCandidates(r._2.stream().map(Candidate::fromWorkflowAssign).collect(Collectors.toList()));
                    return Future.succeededFuture(flowLinkWithCandidates);
                });
        });
    }

    public Future<List<FlowLinkWithCandidates>> nextLinkList(Long taskId) {
        return flowEngineService.nextLinkList(taskId).compose(tuple3 -> {
            FlowInstanceTask task = tuple3._1;
            List<FlowLink> flowLinks = tuple3._3;

            List<SameFutureBuilder<FlowLinkWithCandidates>> taskBuilderList = flowLinks
                .stream()
                .map(flowLink -> this.taskOfQueryCandidates(flowLink, task)).collect(Collectors.toList());
            return CrudUtils.serialTask(taskBuilderList);
        });
    }


    /**
     * 申请者提交
     * @param token 用户令牌
     * @param projectRewardApplyId 申请单Id
     * @return 申请单信息
     */
    public Future<ProjectRewardApply> firstCommit(String token, Long projectRewardApplyId) {
        UserToken userToken = userTokenService.getUserToken(token);
        if (userToken == null) {
            return Future.failedFuture("当前用户未登录，请重新登录");
        }

        return getById(projectRewardApplyId).compose(projectRewardApply -> {
            if (projectRewardApply == null) {
                return Future.failedFuture("当前申请单据已不存在");
            }
            ProjectRewardApply update = new ProjectRewardApply();
            update.setId(projectRewardApplyId);
            update.setStatus(ProjectRewardApplyStatusEnum.COMMITTED.getCode());
            update.setFirstCommitTime(LocalDateTime.now());
            return update(update).map(projectRewardApply);
        }).compose(projectRewardApply -> {
            ContextFlowInstanceStart startContext = new ContextFlowInstanceStart();
            startContext.setStartUser(userToken.getEmployee().getLoginName());
            startContext.setStartUserShow(userToken.getEmployee().getName());
            startContext.setStartType(FlowStartTypeEnum.BY_MANUAL.getCode());
            startContext.setStartTime(LocalDateTime.now());

            Map<String, String> variableTable = new TreeMap<>();
            variableTable.put(FlowConst.FLOW_VARIABLE_BUSINESS_ID, String.valueOf(projectRewardApplyId));
            variableTable.put(FlowConst.FLOW_VARIABLE_BUSINESS_TYPE, "PROJECT_REWARD_APPLY");
            variableTable.put(FlowConst.FLOW_VARIABLE_TITLE, userToken.getEmployee().getName() + "的" + projectRewardApply.getTitle());
            variableTable.put(FlowConst.FLOW_VARIABLE_SENDER, userToken.getEmployee().getLoginName());
            variableTable.put(FlowConst.FLOW_VARIABLE_MODULE_NAME, "专项奖励");
            startContext.setInitVariableTable(variableTable);
            return flowEngineService.startFlow(startContext, "PROJECT_REWARD_APPLY")
                .compose(flowInstance -> {
                    return flowEngineService.getFlowInstanceTaskService().explicitGetOneByLinkCode(flowInstance.getId(), FlowConst.LINK_CODE_APPLY);
                }).compose(task -> {
                    return flowEngineService.completeTask(task.getId());
                })
                .map(projectRewardApply);
        });
    }

    public Future<ProjectRewardApplyVo> getVoById(Long applyId) {
        return getById(applyId).compose(apply -> {
            if (apply == null) {
                return Future.failedFuture("获取申请单信息失败");
            }
            String attachment = apply.getAttachment();
            if (StrUtil.isEmpty(attachment)) {
                return Future.succeededFuture(ProjectRewardApplyVo.fromProjectRewardApply(apply));
            }

            List<Long> attachmentIds = Arrays.stream(attachment.split(StrUtil.COMMA)).map(Long::parseLong).distinct().collect(Collectors.toList());
            return attachmentService.listByIds(attachmentIds).map(list -> {
                ProjectRewardApplyVo vo = ProjectRewardApplyVo.fromProjectRewardApply(apply);
                vo.setAttachmentList(list);
                return vo;
            });
        });
    }

    public Future<Integer> update(ProjectRewardApply projectRewardApply) {
        return projectRewardApplySQL.updateNonEmptyById(projectRewardApply);
    }

    public Future<Integer> deleteWithCheck(long projectRewardApplyId) {
        return getById(projectRewardApplyId).compose(apply -> {
            if (apply == null) {
                return Future.failedFuture("删除申请单时未获取到申请单数据");
            }
            if (ProjectRewardApplyStatusEnum.INIT.getCode() != apply.getStatus()) {
                return Future.failedFuture("只有草稿状态的申请单才能删除");
            }

            return delete(projectRewardApplyId);
        });
    }

    public Future<Integer> delete(long projectRewardApplyId) {
        return projectRewardApplySQL.deleteById(projectRewardApplyId);
    }

    public ProjectRewardApplySQL getProjectRewardApplySQL() {
        return projectRewardApplySQL;
    }

    public void setProjectRewardApplySQL(ProjectRewardApplySQL projectRewardApplySQL) {
        this.projectRewardApplySQL = projectRewardApplySQL;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setUserTokenService(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    public void setFlowEngineService(FlowEngineService flowEngineService) {
        this.flowEngineService = flowEngineService;
    }

    public void setSerialService(SerialService serialService) {
        this.serialService = serialService;
    }

    public void setEmployeeRoleService(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }

    public AttachmentService getAttachmentService() {
        return attachmentService;
    }

    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }
}
