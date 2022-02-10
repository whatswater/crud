package com.whatswater.sql.expression.literal;


import com.whatswater.sql.expression.Literal;

/**
 * 字符串字面量
 * 注意：不要把用户的输入当作字符串常量，应当使用占位符
 * @author whatswater
 *
 */
public class StringValue implements Literal {
    final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
