package com.whatswater.nothing.property;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExcludeProperties implements Properties {
    private Properties baseProperties;
    private Set<String> excludePropertyNames;
    private List<Property<?>> properties;
    private Map<String, Integer> propertyIndex;

    public ExcludeProperties() {
    }

    public ExcludeProperties(Properties baseProperties) {
        this.baseProperties = baseProperties;
    }

    public ExcludeProperties(Properties baseProperties, Set<String> excludePropertyNames) {
        this.baseProperties = baseProperties;
        this.excludePropertyNames = excludePropertyNames;
    }

    @Override
    public void initProperties() {
        List<Property<?>> properties = baseProperties.properties()
            .stream()
            .filter(p -> !excludePropertyNames.contains(p.getPropertyName()))
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
