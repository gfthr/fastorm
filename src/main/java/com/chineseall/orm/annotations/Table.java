package com.chineseall.orm.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String name();
    GeneratorType generate() default GeneratorType.AUTO;
    boolean autoCreatable() default false;
}
