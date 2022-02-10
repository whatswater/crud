package com.whatswater.curd.project.sys.serial;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/sys/serial")
public class SerialRest {
    private final SerialService serialService;

    public SerialRest(SerialService serialService) {
        this.serialService = serialService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Serial>>> search(@BeanParam Page page, SerialQuery query) {
        if (query == null) {
            query = new SerialQuery();
        }
        return serialService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<Serial> get(@QueryParam("id") Long serialId) {
        return serialService.getById(serialId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(Serial serial) {
        return serialService.insert(serial).map(RestResult::success);
    }

    @PUT
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(Serial serial) {
        Assert.assertNotNull(serial.getId(), "Id不能为空");
        return serialService.update(serial).map(RestResult::success);
    }
}
