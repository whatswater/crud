package com.whatswater.sql.table.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableField {
    String value() default "";
    boolean exist() default true;
    FieldStrategy insertStrategy() default FieldStrategy.NOT_NULL;
}
