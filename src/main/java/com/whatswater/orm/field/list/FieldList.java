package com.whatswater.orm.field.list;

import com.whatswater.orm.field.ComputeField;
import com.whatswater.orm.field.ComputeFunction;
import com.whatswater.orm.field.Field;
import com.whatswater.orm.field.FieldType;
import com.whatswater.orm.field.ForeignKeyField;
import com.whatswater.orm.field.OneToManyField;
import com.whatswater.orm.schema.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    default Field findProperty(String propertyName, String type) {
        Field ret = this.findProperty(propertyName);
        if (type.equals(ret.getTypeDescription())) {
            return ret;
        }
        return null;
    }

    default List<Field> listenOn(Schema schema) {
        List<Field> listenOnList = new ArrayList<>();
        for(Field property : this.properties()) {
            FieldType fieldType = property.type();
            if (FieldType.FOREIGN_KEY == fieldType) {
                ForeignKeyField field = (ForeignKeyField) property;
                if (field.getFkSchema() == schema) {
                    listenOnList.add(property);
                }
            } else if (FieldType.ONE_TO_MANY == fieldType) {
                OneToManyField field = (OneToManyField) property;
                if (field.getRefSchema() == schema) {
                    listenOnList.add(property);
                }
            }
        }
        return listenOnList;
    }

    default List<ComputeField> computeBy(Schema schema) {
        List<Field> listenOnList = this.listenOn(schema);
        if (listenOnList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ComputeField> ret = new ArrayList<>();
        for(Field property : this.properties()) {
            FieldType fieldType = property.type();
            if (FieldType.COMPUTE != fieldType) {
                continue;
            }
            ComputeField computeField = (ComputeField) property;
            if (computeField.isListen(listenOnList)) {
                ret.add(computeField);
            }
        }

        return ret;
    }

    void initProperties();
    List<Field> properties();

    Set<String> propertyNames();

    Field findProperty(String propertyName);


}
