package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

import java.util.List;

public class ComputeField implements Field {
    private Schema schema;
    private String type;
    private String propertyName;
    private List<Field> listenProperties;
    private ComputeMethod method;

    @Override
    public FieldType type() {
        return FieldType.COMPUTE;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public String getTypeDescription() {
        return type;
    }

    @Override
    public String getPropertyName() {
        return null;
    }

    public List<Field> getListenProperties() {
        return listenProperties;
    }

    public ComputeMethod getMethod() {
        return method;
    }

    public boolean isListen(List<Field> fieldList) {
        for(Field listenProperty : listenProperties) {
            for(Field field : fieldList) {
                if (listenProperty.getPropertyName().equals(field.getPropertyName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface ComputeMethod {
        Object apply(Object[] data);
    }
}
