package com.whatswater.nothing.property;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PickProperties implements Properties {
    private Properties baseProperties;
    private Set<String> pickPropertyNames;
    private List<Property<?>> properties;
    private Map<String, Integer> propertyIndex;

    public PickProperties() {
    }

    public PickProperties(Properties baseProperties) {
        this.baseProperties = baseProperties;
    }

    public PickProperties(Properties baseProperties, Set<String> pickPropertyNames) {
        this.baseProperties = baseProperties;
        this.pickPropertyNames = pickPropertyNames;
    }

    @Override
    public void initProperties() {
        List<Property<?>> properties = baseProperties.properties()
            .stream()
            .filter(p -> pickPropertyNames.contains(p.getPropertyName()))
            .collect(Collectors.toList());
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
