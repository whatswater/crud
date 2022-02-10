package com.whatswater.curd.project.sys.permission;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.RestResult;
import com.whatswater.curd.project.sys.admin.Admin;
import com.whatswater.curd.project.sys.employee.Employee;
import com.whatswater.curd.project.sys.menu.Menu;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sys/token")
public class UserTokenRest {
    private final UserTokenService userTokenService;

    public UserTokenRest(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @POST
    @Path("/loginInfo")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<UserToken>> loginInfo(@HeaderParam(CrudConst.HEADER_TOKEN) String token) {
        UserToken userToken = userTokenService.getUserToken(token);
        UserToken newToken = new UserToken();

        newToken.setAdmin(userToken.getAdmin());
        newToken.setEmployee(userToken.getEmployee());
        newToken.setUpdateTime(userToken.getUpdateTime());
        newToken.setTtl(userToken.getTtl());

        return Future.succeededFuture(RestResult.success(userToken));
    }

    @POST
    @Path("/getAuthorizedMenuList")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<List<Menu>>> get(@HeaderParam(CrudConst.HEADER_TOKEN) String token) {
        return userTokenService.getAuthorizedMenuList(token).map(RestResult::success);
    }
}
