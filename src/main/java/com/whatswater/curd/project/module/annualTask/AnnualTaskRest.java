package com.whatswater.curd.project.module.annualTask;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.sys.organization.Organization;
import com.whatswater.curd.project.sys.organization.OrganizationQuery;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/business/annualTask")
public class AnnualTaskRest {
    private final AnnualTaskService annualTaskService;

    public AnnualTaskRest(AnnualTaskService annualTaskService) {
        this.annualTaskService = annualTaskService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<AnnualTask>>> search(@BeanParam Page page, AnnualTaskQuery query) {
        if (query == null) {
            query = new AnnualTaskQuery();
        }
        return annualTaskService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<AnnualTask> get(@QueryParam("id") Long annualTaskId) {
        return annualTaskService.getById(annualTaskId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(AnnualTask annualTask) {
        Assert.assertNotNull(annualTask, "年度重点工作任务为空");
        annualTask.setVersionNo(1);
        annualTask.setStatus(AnnualTaskStatus.INIT.getId());
        annualTask.setParentId(0L);
        annualTask.setLevel(1);

        return annualTaskService.insert(annualTask).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> update(AnnualTask annualTask) {
        Assert.assertNotNull(annualTask.getId(), "Id不能为空");
        return annualTaskService.update(annualTask).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long annualTaskId) {
        Assert.assertNotNull(annualTaskId, "Id不能为空");
        return annualTaskService.delete(annualTaskId).map(RestResult::success);
    }
}
