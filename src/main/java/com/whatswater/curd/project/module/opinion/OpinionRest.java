package com.whatswater.curd.project.module.opinion;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.sys.permission.UserToken;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/business/opinion")
public class OpinionRest {
    private final OpinionService opinionService;

    public OpinionRest(OpinionService opinionService) {
        this.opinionService = opinionService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Opinion>>> search(@BeanParam Page page, OpinionQuery query) {
        if (query == null) {
            query = new OpinionQuery();
        }
        return opinionService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/queryByFlowInstanceId")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<List<OpinionVo>>> queryByFlowInstanceId(@QueryParam("instanceId") Long instanceId) {
        return opinionService.queryVoByFlowInstanceId(instanceId).map(RestResult::success);
    }


    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<Opinion> get(@QueryParam("id") Long opinionId) {
        return opinionService.getById(opinionId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(@HeaderParam(CrudConst.HEADER_TOKEN) String token, Opinion opinion) {
        return opinionService.fillAndInsertWithCheck(token, opinion).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(@HeaderParam(CrudConst.HEADER_TOKEN) String token, Opinion opinion) {
        Assert.assertNotNull(opinion.getId(), "Id不能为空");
        return opinionService.updateWithCheck(token, opinion).map(RestResult::success);
    }
}
