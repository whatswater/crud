package com.whatswater.orm.schema;

import com.whatswater.orm.schema.index.Index;

import java.util.List;

// Index类中的方法被称作action
// delete、insert、update、move应该被称作operation，触发监听
// Index中的方法可以任意定义
// Index方法调用operation
// 监听可划分为多个轮次，先执行第一轮
// delete、insert、update、move先被storageService记录下，lazy的方式提交

// SchemaDataService主要处理监听的问题
// Index处理数据操作和Action相关的东西
// Index触发一个ACTION时，同时触发事件
// SCHEMA的数据代理类
public class SchemaDataService {
//    private Index mainIndex;
//    private List<Index> indexServiceList;
//
//    public Index getIndex() {
//
//    }


//    SchemaDataService
}
