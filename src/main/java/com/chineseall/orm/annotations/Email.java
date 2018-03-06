package com.chineseall.orm.annotations;

import com.chineseall.orm.validators.EmailValidator;

import java.lang.annotation.*;

@ValidatorClass(EmailValidator.class)
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Email {
    String message() default "非法电子邮件格式";
}
