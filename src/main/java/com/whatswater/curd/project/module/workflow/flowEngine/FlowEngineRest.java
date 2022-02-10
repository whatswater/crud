package com.whatswater.curd.project.module.workflow.flowEngine;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.module.workflow.flowInstanceTask.FlowInstanceTask;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLink;
import com.whatswater.curd.project.module.workflow.flowLink.FlowLinkWithCandidates;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/workflow/flowEngine")
public class FlowEngineRest {
    private final FlowEngineService flowEngineService;

    public FlowEngineRest(FlowEngineService flowEngineService) {
        this.flowEngineService = flowEngineService;
    }

    @POST
    @Path("/deployFlow")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> search(@QueryParam("flowDefinitionId") Long flowDefinitionId) {
        Assert.assertNotNull(flowDefinitionId, "流程Id不能为空");
        return flowEngineService.deployFlow(flowDefinitionId).map(RestResult::success);
    }

    @POST
    @Path("/commit")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<FlowInstanceTask>> commit(@QueryParam("taskId") Long taskId, NextLinkActorInfo nextLinkActorInfo) {
        return flowEngineService.commit(taskId, nextLinkActorInfo).map(RestResult::success);
    }
}
