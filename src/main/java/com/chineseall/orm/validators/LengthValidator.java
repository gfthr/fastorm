package com.chineseall.orm.validators;

import com.chineseall.orm.annotations.Length;

/**
 * 长度验证类
 * @author stworthy
 */
public class LengthValidator extends AbstractValidator<Length> {
    public boolean validate(Object value) {
        String s = (String)value;
        if (s == null){
            s = "";
        }
        if (s.length() < parameters.min() || s.length() > parameters.max()){
            return false;
        }
        else{
            return true;
        }
    }
}
