package com.whatswater.curd.project.common;


import javax.ws.rs.QueryParam;

public class Page {
    private final int currentPage;
    private final int pageSize;

    public Page(@QueryParam("currentPage") int currentPage, @QueryParam("pageSize") int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getLimit() {
        return pageSize;
    }

    public int getOffset() {
        return (currentPage - 1) * pageSize;
    }

    public static Page createPage() {
        return new Page(1, 10);
    }

    public static Page createPage(int currentPage) {
        return new Page(currentPage, 10);
    }

    public static Page createPage(int currentPage, int pageSize) {
        return new Page(currentPage, pageSize);
    }
}
