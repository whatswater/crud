package com.whatswater.sql.table.annotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableId {
    IdType type() default IdType.NONE;
}
