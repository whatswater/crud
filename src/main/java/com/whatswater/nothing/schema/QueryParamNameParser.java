package com.whatswater.nothing.schema;

import com.whatswater.nothing.property.Properties;
import com.whatswater.nothing.property.Property;
import com.whatswater.sql.expression.BoolExpression;
import com.whatswater.sql.utils.StringUtils;


/**
 * 解析参数名称
 */
public abstract class QueryParamNameParser {
    public static class PropertyWithOperation {
        private QueryParamNameOperationEnum operation;
        private Property<?> property;

        public PropertyWithOperation(QueryParamNameOperationEnum operation, Property<?> property) {
            this.operation = operation;
            this.property = property;
        }

        public QueryParamNameOperationEnum getOperation() {
            return operation;
        }

        public Property<?> getProperty() {
            return property;
        }

        // todo 当paramValue为null的处理
        // todo 实现转换为paramValue
        public BoolExpression toBoolExpression(Object paramValue) {
            return null;
        }
    }

    public static PropertyWithOperation parseName(Properties properties, String queryParamName) {
        String[] t = queryParamName.split(StringUtils.BLANK);
        if (t.length != 2) {
            return null;
        }
        String propertyName = t[0];
        Property<?> property = properties.findProperty(propertyName);
        if (property == null) {
            return null;
        }
        String operationName = t[1];
        QueryParamNameOperationEnum op = QueryParamNameOperationEnum.get(operationName);
        if (op == null) {
            return null;
        }

        return new PropertyWithOperation(op, property);
    }
}
