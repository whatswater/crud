package com.whatswater.curd.project.module.workflow.flowDefinition;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.*;
import com.whatswater.curd.project.common.LoadPageData.DictItem;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

@Path("/workflow/flowDefinition")
public class FlowDefinitionRest {
    private final FlowDefinitionService flowDefinitionService;

    public FlowDefinitionRest(FlowDefinitionService flowDefinitionService) {
        this.flowDefinitionService = flowDefinitionService;
    }

    @POST
    @Path("/loadPage")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<LoadPageData>> loadPage(@QueryParam("pageId") String pageId) {
        List<DictItem> dictItemList = FlowDefinitionStatusEnum.toDictItemList();
        LoadPageData loadPageData = LoadPageData.of(Arrays.asList("add", "edit", "delete")).addDict("status", dictItemList);
        return Future.succeededFuture(RestResult.success(loadPageData));
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<FlowDefinition>>> search(@BeanParam Page page, FlowDefinitionQuery query) {
        if (query == null) {
            query = new FlowDefinitionQuery();
        }
        return flowDefinitionService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<FlowDefinition>> get(@QueryParam("id") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "Id不能为空");
        return flowDefinitionService.getById(flowDefinitionId).map(RestResult::success);
    }

    @POST
    @Path("/geGraph")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<FlowDefinitionGraph>> geGraph(@QueryParam("id") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "Id不能为空");
        return flowDefinitionService.getJsonByFlowDefinitionId(flowDefinitionId).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(FlowDefinitionVO flowDefinitionVo) {
        Assert.assertNotNull(flowDefinitionVo, "数据不能为空");
        return flowDefinitionService.insertVo(flowDefinitionVo).map(RestResult::success);
    }

    @POST
    @Path("/enableOrInitFlowDefinition")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> enableOrInitFlowDefinition(@QueryParam("id") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "Id不能为空");
        return flowDefinitionService.enableOrInitFlowDefinition(flowDefinitionId).map(RestResult::success);
    }

    @POST
    @Path("/disableOrDraftFlowDefinition")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> disableOrDraftFlowDefinition(@QueryParam("id") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "Id不能为空");
        return flowDefinitionService.disableOrDraftFlowDefinition(flowDefinitionId).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "Id不能为空");
        return flowDefinitionService.deleteWithCheck(flowDefinitionId).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(FlowDefinitionVO flowDefinitionVo) {
        Assert.assertNotNull(flowDefinitionVo.getId(), "Id不能为空");
        return flowDefinitionService.updateVo(flowDefinitionVo).map(RestResult::success);
    }
}
