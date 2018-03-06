package com.chineseall.orm.annotations;

import com.chineseall.orm.validators.PatternValidator;

import java.lang.annotation.*;

@ValidatorClass(PatternValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pattern {
    String regex();
    String message() default "模式匹配错误";
}
