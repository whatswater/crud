package com.whatswater.curd.project.user;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    public String get(@QueryParam("userId") String userId) {
        try {
            return userService.updateNameById();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
