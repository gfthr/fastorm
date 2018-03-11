package com.chineseall.orm.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String name();
    GeneratorType generate() default GeneratorType.AUTO;
    boolean autoCreatable() default false;
    ModelEngineType engine() default ModelEngineType.CACHE_MYSQL_OBJECT;
    String view() default "";
    String deleteMark() default "";
    String column() default "";
}
