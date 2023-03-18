package com.whatswater.orm.field;

import com.whatswater.orm.schema.Schema;

import java.util.Collections;
import java.util.List;

public class ComputeField implements Field {
    private Schema<?> schema;
    private String type;
    private String propertyName;
    private List<BasicField> listenProperties;
    private ComputeMethod method;

    @Override
    public Schema<?> getSchema() {
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

    public interface ComputeMethod {
        Object apply(Object[] data);
    }

    public static class ComputePropertiesBuilder1 {
        private List<BasicField> listenProperties;

        public  ComputePropertiesBuilder2 compute(ComputeMethod computeMethod) {
            ComputePropertiesBuilder2 builder2 = new ComputePropertiesBuilder2();
            builder2.listenProperties = listenProperties;
            builder2.computeMethod = computeMethod;
            return builder2;
        }
    }

    public static class ComputePropertiesBuilder2 {
        private List<BasicField> listenProperties;
        private ComputeMethod computeMethod;

        public ComputeField name(Schema<?> schema, String name, String type) {
            ComputeField properties = new ComputeField();
            properties.listenProperties = listenProperties;
            properties.method = computeMethod;
            properties.schema = schema;
            properties.type = type;
            properties.propertyName = name;
            return properties;
        }

    }

    public static ComputePropertiesBuilder1 watch(BasicField properties) {
        ComputePropertiesBuilder1 computePropertiesBuilder = new ComputePropertiesBuilder1();
        computePropertiesBuilder.listenProperties = Collections.singletonList(properties);
        return computePropertiesBuilder;
    }
}
