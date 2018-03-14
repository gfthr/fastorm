package com.chineseall.orm.proxy;

/**
 * Created by wangqiang on 2018/3/14.
 */

import com.chineseall.orm.Model;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseTypeHandler implements InvocationHandler {
    private Object object;
    private Object parentObject;
    private String key;

    private boolean modified = false;
    protected static Set<String> modifyMethodSet =new HashSet();

    public BaseTypeHandler(Object object, Object parentObject, String key) {
        this.object = object;
        this.parentObject =parentObject;
        this.key=key;
    }

    protected static void splitModifyMethodStr(String modifyMethodStr){
        String[] methods = StringUtils.split(modifyMethodStr,",");
        for (String name:methods
                ) {
            modifyMethodSet.add(name);
        }
    }

    public void markModified() {
        this.modified = true;
        if(parentObject!=null && parentObject instanceof Model){
            ((Model)parentObject).addChildModified(this.key);
        }
    }

    public void markFlushed() {
        this.modified = false;
    }

    public boolean isModified() {
        return this.modified;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(modifyMethodSet.contains(method.getName())){
            markModified();
        }
        return method.invoke(object, args);
    }

}