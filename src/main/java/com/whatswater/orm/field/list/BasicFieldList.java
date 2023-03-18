package com.whatswater.orm.field.list;


import com.whatswater.orm.field.BasicField;
import com.whatswater.orm.field.Field;

import java.util.*;

public class BasicFieldList implements FieldList {
    private final List<Field> properties;
    private Map<String, Integer> propertyIndex;

    public BasicFieldList(List<BasicField> properties) {
        this.properties = Collections.unmodifiableList(properties);
    }

    public BasicFieldList(FieldList properties) {
        this.properties = Collections.unmodifiableList(properties.properties());
    }

    @Override
    public void initProperties() {
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
