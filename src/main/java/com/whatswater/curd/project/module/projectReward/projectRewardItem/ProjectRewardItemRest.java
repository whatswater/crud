package com.whatswater.curd.project.module.projectReward.projectRewardItem;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/business/projectRewardItem")
public class ProjectRewardItemRest {
    private final ProjectRewardItemService projectRewardItemService;

    public ProjectRewardItemRest(ProjectRewardItemService projectRewardItemService) {
        this.projectRewardItemService = projectRewardItemService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<ProjectRewardItemVoOfList>>> search(@BeanParam Page page, ProjectRewardItemQuery query) {
        if (query == null) {
            query = new ProjectRewardItemQuery();
        }
        return projectRewardItemService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<ProjectRewardItem>> get(@QueryParam("id") Long projectRewardItemId) {
        return projectRewardItemService.getById(projectRewardItemId).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(ProjectRewardItem projectRewardItem) {
        Assert.assertNotNull(projectRewardItem.getCategoryId(), "激励内容不能为空");
        Assert.assertNotEmpty(projectRewardItem.getItemName(), "子项目名称不能为空");
        Assert.assertNotEmpty(projectRewardItem.getStandard(), "标准不能为空");

        return projectRewardItemService.fillAndInsertWithCheck(projectRewardItem).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(ProjectRewardItem projectRewardItem) {
        Assert.assertNotNull(projectRewardItem.getId(), "Id不能为空");
        Assert.assertNotEmpty(projectRewardItem.getItemName(), "子项目名称不能为空");
        Assert.assertNotEmpty(projectRewardItem.getStandard(), "标准不能为空");

        return projectRewardItemService.updateWithCheck(projectRewardItem).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long projectRewardItemId) {
        Assert.assertNotNull(projectRewardItemId, "Id不能为空");

        return projectRewardItemService.deleteWithCheck(projectRewardItemId).map(RestResult::success);
    }
}
