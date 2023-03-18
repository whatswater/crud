package com.whatswater.orm.field.list;

import com.whatswater.orm.field.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public interface FieldList {
    default Map<String, Integer> buildPropertyIndex() {
        Map<String, Integer> propertyIndex =  new TreeMap<>();
        List<Field> properties = properties();
        for (int i = 0; i < properties.size(); i++) {
            propertyIndex.put(properties.get(i).getPropertyName(), i);
        }
        return propertyIndex;
    }


    void initProperties();
    List<Field> properties();

    Set<String> propertyNames();

    Field findProperty(String propertyName);
}
