package com.whatswater.curd.project.common;


import java.util.List;

public class PageResult<T> {
    private static final Page EMPTY_PAGE = Page.createPage();

    private List<T> data;
    private Long total;
    private Page page;

    public List<T> getData() {
        return data;
    }

    public Long getTotal() {
        return total;
    }

    public Page getPage() {
        return page;
    }

    public static <T> PageResult<T> empty() {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.data = CrudUtils.emptyList();
        pageResult.total = 0L;
        pageResult.page = EMPTY_PAGE;
        return pageResult;
    }

    public static <T> PageResult<T> of(List<T> data, Page page, Long total) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.data = data;
        pageResult.total = total;
        pageResult.page = page;
        return pageResult;
    }

    public static <T> PageResult<T> of(List<T> data, PageResult<?> old) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.data = data;
        pageResult.total = old.total;
        pageResult.page = old.page;
        return pageResult;
    }
}
