package com.chineseall.orm.annotations;

import com.chineseall.orm.validators.LengthValidator;

import java.lang.annotation.*;

@ValidatorClass(LengthValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Length{
    int min() default 0;
    int max();
    String message();
}
