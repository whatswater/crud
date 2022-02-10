package com.whatswater.curd.project.module.projectReward.projectRewardCategory;

import cn.hutool.core.collection.CollectionUtil;
import com.whatswater.curd.CrudConst;
import com.whatswater.curd.project.common.*;
import com.whatswater.curd.project.common.LoadPageData.DictItem;
import io.vertx.core.Future;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@Path("/business/projectRewardCategory")
public class ProjectRewardCategoryRest {
    private final ProjectRewardCategoryService projectRewardCategoryService;

    public ProjectRewardCategoryRest(ProjectRewardCategoryService projectRewardCategoryService) {
        this.projectRewardCategoryService = projectRewardCategoryService;
    }

    @POST
    @Path("/loadPage")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<LoadPageData>> loadPage(@QueryParam("pageId") String pageId) {
        List<DictItem> dictItemList = new ArrayList<>();
        LoadPageData loadPageData = LoadPageData.of(Arrays.asList("add", "edit", "delete")).addDict("level", dictItemList);
        return Future.succeededFuture(RestResult.success(loadPageData));
    }

    @POST
    @Path("/search")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<PageResult<ProjectRewardCategoryWithChildren>>> search(@BeanParam Page page, ProjectRewardCategoryQuery query) {
        if (query == null) {
            query = new ProjectRewardCategoryQuery();
        }
        if (Objects.isNull(query.getLevel())) {
            query.setLevel(1);
        }
        return projectRewardCategoryService.search(page, query).compose(pageResult -> {
            List<ProjectRewardCategory> categoryList = pageResult.getData();
            if (CollectionUtil.isEmpty(categoryList)) {
                PageResult<ProjectRewardCategoryWithChildren> newPageResult = PageResult.of(Collections.emptyList(), pageResult);
                return Future.succeededFuture(newPageResult);
            }
            List<Long> idList = categoryList.stream().map(ProjectRewardCategory::getId).collect(Collectors.toList());
            return projectRewardCategoryService.queryChildren(idList).<PageResult<ProjectRewardCategoryWithChildren>>map(children -> {
                if (CollectionUtil.isEmpty(children)) {
                    children = Collections.emptyList();
                }
                Map<Long, List<ProjectRewardCategory>> grouped = children.stream().collect(Collectors.groupingBy(ProjectRewardCategory::getParentId));
                List<ProjectRewardCategoryWithChildren> withChildrenList = categoryList.stream()
                    .map(category -> ProjectRewardCategoryWithChildren.fromCategory(category, grouped.get(category.getId())))
                    .collect(Collectors.toList());
                return PageResult.of(withChildrenList, pageResult);
            });
        }).map(RestResult::success);
    }

    @POST
    @Path("/get")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<ProjectRewardCategory>> get(@QueryParam("id") Long projectRewardCategoryId) {
        return projectRewardCategoryService.getById(projectRewardCategoryId).map(RestResult::success);
    }

    @POST
    @Path("/insert")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Long>> insert(ProjectRewardCategory projectRewardCategory) {
        return projectRewardCategoryService.fillAndInsertWithCheck(projectRewardCategory).map(RestResult::success);
    }

    @POST
    @Path("/update")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Future<RestResult<Integer>> update(ProjectRewardCategory projectRewardCategory) {
        Assert.assertNotNull(projectRewardCategory.getId(), "Id不能为空");
        return projectRewardCategoryService.update(projectRewardCategory).map(RestResult::success);
    }

    @POST
    @Path("/delete")
    @Produces(CrudConst.APPLICATION_JSON_UTF8)
    public Future<RestResult<Integer>> update(@QueryParam("id") Long id) {
        Assert.assertNotNull(id, "Id不能为空");
        return projectRewardCategoryService.deleteWithCheck(id).map(RestResult::success);
    }
}
