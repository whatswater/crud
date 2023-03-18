package com.whatswater.orm.field.list;


import com.whatswater.orm.field.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MergeFieldList implements FieldList {
    /**
     * 原model
     */
    private final FieldList baseProperties;
    /**
     * 合并的model
     */
    private FieldList mergedProperties;
    /**
     * 两个对象属性分界线
     */
    private int splitIndex;

    /**
     * 对象的属性，只读
     */
    private List<Field> properties;
    private Map<String, Integer> propertyIndex;


    public MergeFieldList(FieldList baseProperties) {
        this.baseProperties = baseProperties;
    }

    public MergeFieldList(FieldList baseProperties, FieldList mergedProperties) {
        this.baseProperties = baseProperties;
        this.mergedProperties = mergedProperties;
        initProperties();
    }

    @Override
    public void initProperties() {
        List<Field> baseProperties = this.baseProperties.properties();
        List<Field> mergedProperties = this.mergedProperties.properties();

        List<Field> properties = new ArrayList<>(mergedProperties);
        Set<String> propertyNames = this.mergedProperties.propertyNames();
        for (Field property: baseProperties) {
            String propertyName = property.getPropertyName();
            if (!propertyNames.contains(propertyName)) {
                properties.add(property);
            }
        }
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
