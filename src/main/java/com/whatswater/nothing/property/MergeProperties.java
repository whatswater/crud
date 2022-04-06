package com.whatswater.nothing.property;


import java.util.*;

public class MergeProperties implements Properties {
    /**
     * 原model
     */
    private final Properties baseProperties;
    /**
     * 合并的model
     */
    private Properties mergedProperties;
    /**
     * 两个对象属性分界线
     */
    private int splitIndex;

    /**
     * 对象的属性，只读
     */
    private List<Property<?>> properties;
    private Map<String, Integer> propertyIndex;


    public MergeProperties(Properties baseProperties) {
        this.baseProperties = baseProperties;
    }

    public MergeProperties(Properties baseProperties, Properties mergedProperties) {
        this.baseProperties = baseProperties;
        this.mergedProperties = mergedProperties;
        initProperties();
    }

    @Override
    public void initProperties() {
        List<Property<?>> baseProperties = this.baseProperties.properties();
        List<Property<?>> mergedProperties = this.mergedProperties.properties();

        List<Property<?>> properties = new ArrayList<>(mergedProperties);
        Set<String> propertyNames = this.mergedProperties.propertyNames();
        for (Property<?> property: baseProperties) {
            String propertyName = property.getPropertyName();
            if (!propertyNames.contains(propertyName)) {
                properties.add(property);
            }
        }
        this.properties = Collections.unmodifiableList(properties);
        this.propertyIndex = buildPropertyIndex();
    }

    @Override
    public List<Property<?>> properties() {
        return properties;
    }

    @Override
    public Set<String> propertyNames() {
        return propertyIndex.keySet();
    }

    @Override
    public Property<?> findProperty(String propertyName) {
        Integer index = propertyIndex.get(propertyName);
        if (index == null) {
            return null;
        }
        return properties.get(index);
    }
}
