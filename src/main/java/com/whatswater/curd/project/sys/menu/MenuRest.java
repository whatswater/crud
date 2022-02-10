package com.whatswater.curd.project.sys.menu;

import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.Assert;
import com.whatswater.curd.project.common.Page;
import com.whatswater.curd.project.common.PageResult;
import com.whatswater.curd.project.common.RestResult;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/sys/menu")
public class MenuRest {
    private final MenuService menuService;

    public MenuRest(MenuService menuService) {
        this.menuService = menuService;
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<Menu>>> search(@BeanParam Page page, MenuQuery query) {
        if (query == null) {
            query = new MenuQuery();
        }
        return menuService.search(page, query).map(RestResult::success);
    }

    @POST
    @Path("/queryChildrenName")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<List<MenuName>>> queryChildrenName(@QueryParam("id") Long id) {
        Assert.assertNotNull(id, "Id不能为空");
        return menuService.queryChildrenName(id).map(RestResult::success);
    }


    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<Menu> get(@QueryParam("id") Long menuId) {
        return menuService.getById(menuId);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(Menu menu) {
        Assert.assertNotEmpty(menu.getName(), "菜单的名称不能为空");
        return menuService.fillAndInsertWithCheck(menu).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(MenuName menu) {
        Assert.assertNotNull(menu.getId(), "菜单Id不能为空");
        return menuService.updateWithCheck(menu).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> delete(@QueryParam("id") Long id) {
        Assert.assertNotNull(id, "Id不能为空");
        return menuService.deleteWithCheck(id).map(RestResult::success);
    }
}
