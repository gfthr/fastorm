package com.chineseall.orm.proxy;

/**
 * Created by wangqiang on 2018/3/14.
 */

public class MapProxy extends BaseTypeProxy {
    static {
        String modifyMethodStr="put,remove,putAll,clear,replaceAll,putIfAbsent,remove,replace,replace";
        splitModifyMethodStr(modifyMethodStr);
    }

    public MapProxy(Object object,Object parentObject, String key) {
        super(object,parentObject,key);
    }
}