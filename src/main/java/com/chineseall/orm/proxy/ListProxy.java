package com.chineseall.orm.proxy;

/**
 * Created by wangqiang on 2018/3/14.
 */

import java.lang.reflect.InvocationHandler;

public class ListProxy extends BaseTypeProxy implements InvocationHandler{
    static {
        String modifyMethodStr="add,remove,addAll,removeAll,retainAll,replaceAll,sort,clear,set";
        splitModifyMethodStr(modifyMethodStr);
    }

    public ListProxy(Object object,Object parentObject, String key) {
        super(object,parentObject,key);
    }
}