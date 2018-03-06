package com.chineseall.orm.annotations;

import com.chineseall.orm.validators.NotEmptyValidator;

import java.lang.annotation.*;

@ValidatorClass(NotEmptyValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotEmpty {
    String message() default "字段内容不能为空";
}
