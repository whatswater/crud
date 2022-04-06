package com.whatswater.curd.project.template;

import com.whatswater.curd.project.common.LoadPageData.DictItem;
import com.whatswater.curd.project.common.Page;

import java.util.List;
import java.util.Map;

public class BaseModelSearchResult {
    private BaseModel baseModel;
    private List<Object[]> data;
    private Map<String, List<DictItem>> dictData;
    private Page page;
}
