package com.whatswater.nothing.property;


import java.util.*;

public interface Properties {
    default Map<String, Integer> buildPropertyIndex() {
        List<Property<?>> properties = properties();
        Map<String, Integer> propertyIndex =  new TreeMap<>();
        for (int i = 0; i < properties.size(); i++) {
            propertyIndex.put(properties.get(i).getPropertyName(), i);
        }
        return propertyIndex;
    }

    void initProperties();

    /**
     * 当前model包含的所有的属性列表，只读
     * @return 属性列表
     */
    List<Property<?>> properties();

    /**
     * 当前model包含的所有的属性名称Set
     * @return 属性名称Set
     */
    Set<String> propertyNames();

    /**
     * 根据propertyName查找Property，找不到时返回null
     * @param propertyName 属性名称
     * @return 属性
     */
    Property<?> findProperty(String propertyName);

    /**
     * 排除model属性后，形成新model
     * @param propertyNames 属性名称
     * @return 新的model
     */
    default Properties exclude(String... propertyNames) {
        return exclude(Arrays.asList(propertyNames));
    }
    default Properties exclude(List<String> propertyNames) {
        return exclude(new TreeSet<>(propertyNames));
    }
    default Properties exclude(Set<String> propertyNames) {
        return new ExcludeProperties(this, propertyNames);
    }

    /**
     * 合并一个新model
     * @param properties 被合并的model
     * @return 合并后的新model
     */
    default Properties merge(Properties properties) {
        return new MergeProperties(this, properties);
    }

    /**
     * 从原model中选择几个属性，组成一个新model
     * @param propertyNames 属性名称
     * @return 选择的属性组成的新model
     */
    default Properties pick(String ...propertyNames) {
        return pick(Arrays.asList(propertyNames));
    }
    default Properties pick(List<String> propertyNames) {
        return pick(new TreeSet<>(propertyNames));
    }
    default Properties pick(Set<String> propertyNames) {
        return new PickProperties(this, propertyNames);
    }
}
