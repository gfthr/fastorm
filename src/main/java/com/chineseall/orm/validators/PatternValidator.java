package com.chineseall.orm.validators;

import com.chineseall.orm.annotations.Pattern;


/**
 * 模式匹配验证类
 * @author stworthy
 */
public class PatternValidator extends AbstractValidator<Pattern> {
    public boolean validate(Object value){
        String s = (String)value;
        if (s == null){
            s = "";
        }
        if (!s.matches(parameters.regex())){
            return false;
        }
        else{
            return true;
        }
    }
}
