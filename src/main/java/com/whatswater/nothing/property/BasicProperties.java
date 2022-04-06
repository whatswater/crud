package com.whatswater.nothing.property;


import java.util.*;

public class BasicProperties implements Properties {
    /**
     * 对象属性列表，只读
     */
    private List<Property<?>> properties;
    private Map<String, Integer> propertyIndex;

    public BasicProperties(List<Property<?>> properties) {
        this.properties = Collections.unmodifiableList(properties);
    }

    public int getPropertyIndex(Property<?> property) {
        return propertyIndex.get(property.getPropertyName());
    }

    @Override
    public void initProperties() {
        this.propertyIndex = this.buildPropertyIndex();
    }

    @Override
    public List<Property<?>> properties() {
        return Collections.unmodifiableList(properties);
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
