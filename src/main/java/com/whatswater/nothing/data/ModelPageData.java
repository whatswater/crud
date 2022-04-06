package com.whatswater.nothing.data;


public class ModelPageData {
    ModelDataList data;
    private Long total;
    private Page page;

    public ModelDataList getData() {
        return data;
    }

    public Long getTotal() {
        return total;
    }

    public Page getPage() {
        return page;
    }

    public static ModelPageData empty() {
        ModelPageData modelPageData = new ModelPageData();
        modelPageData.data = ModelDataList.EMPTY;
        modelPageData.total = 0L;
        modelPageData.page = Page.EMPTY;
        return modelPageData;
    }

    public static ModelPageData of(ModelDataList data, Page page, Long total) {
        ModelPageData pageResult = new ModelPageData();
        pageResult.data = data;
        pageResult.total = total;
        pageResult.page = page;
        return pageResult;
    }

    public static ModelPageData of(ModelDataList data, ModelPageData old) {
        ModelPageData modelPageData = new ModelPageData();
        modelPageData.data = data;
        modelPageData.total = old.total;
        modelPageData.page = old.page;
        return modelPageData;
    }
}
