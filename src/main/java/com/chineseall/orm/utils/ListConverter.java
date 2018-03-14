package com.chineseall.orm.utils;

import com.alibaba.fastjson.JSON;


/**
 * Created by wangqiang on 2018/3/14.
 */
public class ListConverter implements Converter {

    public Object convert(Object obj){
        String typeName = obj.getClass().getCanonicalName();
        if (typeName.equals("java.lang.String")){
            return JSON.parse((String) obj);
        }else{
            return obj;
        }
    }

}
