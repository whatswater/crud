package com.whatswater.orm.field.list;

import com.whatswater.orm.field.Field;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PickedFieldList implements FieldList {
    private final FieldList baseProperties;
    private Set<String> pickPropertyNames;
    private List<Field> properties;
    private Map<String, Integer> propertyIndex;

    public PickedFieldList(FieldList baseProperties) {
        this.baseProperties = baseProperties;
    }

    public PickedFieldList(FieldList baseProperties, Set<String> pickPropertyNames) {
        this.baseProperties = baseProperties;
        this.pickPropertyNames = pickPropertyNames;
    }

    @Override
    public void initProperties() {
        List<Field> properties = baseProperties.properties()
            .stream()
            .filter(p -> pickPropertyNames.contains(p.getPropertyName()))
            .collect(Collectors.toList());
        this.properties = Collections.unmodifiableList(properties);
        this.propertyIndex = buildPropertyIndex();
    }

    @Override
    public List<Field> properties() {
        return properties;
    }

    @Override
    public Set<String> propertyNames() {
        return propertyIndex.keySet();
    }

    @Override
    public Field findProperty(String propertyName) {
        Integer index = propertyIndex.get(propertyName);
        if (index == null) {
            return null;
        }
        return properties.get(index);
    }
}
