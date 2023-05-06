package com.whatswater.orm.schema;


import com.whatswater.orm.data.id.DataId;
import com.whatswater.orm.field.list.FieldList;

import java.util.List;

// insert时，触发的某个地方的属性变化，应该通过什么计算？
// 将Properties的方法转移至Schema
// Schema之间存在着以下几种关系：UNION（只读）、SUBSET（读写）
// 数据可以从一个schemaDataList移动只另外一个SchemaDataList中
// 一个数据是否能够属于多个SchemaDataList？只能存在多个UNION的数据中
// 除了Schema之外，应当存在另外一种表示数据结构的方式，通过SchemaData和SchemaDataList转换而来（只读），Schema依赖于存储，其他的不依赖存储，是否需要在schema中定义索引
// Schema除了是list外，还可以是map和标量
// Property应当不包含泛型，Properties包含泛型
public interface Schema {
    /**
     * 所在的模块名称，模块名称是模块的唯一ID
     * @return 模块名称
     */
    String moduleName();

    /**
     * Schema名称，用作SchemaDataListService的bean实例名称
     * @return schema名称
     */
    String schemaName();

    /**
     * 当前schema引用的其他schema
     * @return schema列表
     */
    List<Schema> refSchemaList();

    /**
     * 当前schema监听的其他schema列表
     * @return schema列表
     */
    List<Schema> listenSchemaList();

    /**
     * 获取数据的主键值
     * @return 主键
     */
    DataId getPrimaryKeyValue(Object data);

    /**
     * 字段列表
     * @return 字段列表
     */
    FieldList fieldList();
}
