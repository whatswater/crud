package com.whatswater.curd.project.module.projectReward.projectRewardApply;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithCandidates;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/business/projectRewardApply")
public class ProjectRewardApplyRest {
    private final ProjectRewardApplyService projectRewardApplyService;
    public ProjectRewardApplyRest(ProjectRewardApplyService projectRewardApplyService) {
        this.projectRewardApplyService = projectRewardApplyService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<ProjectRewardApply>>> search(@BeanParam Page page, ProjectRewardApplyQuery query) {
        if (query == null) {
            query = new ProjectRewardApplyQuery();
        }
        return projectRewardApplyService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/initApply")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<ProjectRewardApply>> initApply(@HeaderParam(CrudConst.HEADER_TOKEN) String token, @QueryParam("id") Long applyId) {
        if (applyId != null && applyId > 0L) {
            return projectRewardApplyService.initApply(applyId).map(RestResult::success);
        }
        return projectRewardApplyService.initApply(token).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<ProjectRewardApply> get(@QueryParam("id") Long projectRewardApplyId) {
        return projectRewardApplyService.getById(projectRewardApplyId);
    }

    @POST
    @Path("/getVoById")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<ProjectRewardApplyVo>> getVoById(@QueryParam("id") Long projectRewardApplyId) {
        return projectRewardApplyService.getVoById(projectRewardApplyId).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(@HeaderParam(CrudConst.HEADER_TOKEN) String token, ProjectRewardApply projectRewardApply) {
        return projectRewardApplyService.fillAndInsertWithCheck(token, projectRewardApply).map(RestResult::success);
    }

    @POST
    @Path("/nextLinkList")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<FlowLinkWithCandidates>>> nextLinkList(@QueryParam("taskId") Long taskId) {
        return projectRewardApplyService.nextLinkList(taskId).map(RestResult::success);
    }

    @POST
    @Path("/firstCommit")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<ProjectRewardApply>> firstCommit(@HeaderParam(CrudConst.HEADER_TOKEN) String token, @QueryParam("id") Long projectRewardApplyId) {
        return projectRewardApplyService.firstCommit(token, projectRewardApplyId).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(ProjectRewardApply projectRewardApply) {
        Assert.assertNotNull(projectRewardApply.getId(), "Id不能为空");
        return projectRewardApplyService.update(projectRewardApply).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> update(@QueryParam("id") Long projectRewardApplyId) {
        Assert.assertNotNull(projectRewardApplyId, "Id不能为空");
        return projectRewardApplyService.deleteWithCheck(projectRewardApplyId).map(RestResult::success);
    }
}
