package com.whatswater.nothing.data;


public class Page {
    public static final Page EMPTY = new Page();
    private int pageSize = 10;
    private int currentPage = 1;

    private Page() {
    }

    public Page(int pageSize, int currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
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

    public static Page createPage(int currentPage) {
        return new Page(currentPage, 10);
    }

    public static Page createPage(int currentPage, int pageSize) {
        return new Page(currentPage, pageSize);
    }
}
