package com.whatswater.curd.project.user;


import com.zandero.rest.annotation.BodyParam;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserRest {
    public static final String APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=utf-8";
    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/get")
    @Produces(APPLICATION_JSON_UTF8)
    public Future<User> get(@QueryParam("userId") Long userId) {
        if (userId == null) {
            throw new RuntimeException("11111111111111111");
        }
        try {
            return userService.getById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Future.failedFuture(e);
        }
    }

    @POST
    @Path("/put")
    @Produces(APPLICATION_JSON_UTF8)
    public Future<Long> put(@BodyParam User user) {
        if (user == null) {
            throw new RuntimeException("11111111111111111");
        }
        try {
            return userService.insert(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Future.failedFuture(e);
        }
    }
}
