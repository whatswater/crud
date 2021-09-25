package com.whatswater.sql.table.annotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableName {
    String value();
    String schema() default "";
}
