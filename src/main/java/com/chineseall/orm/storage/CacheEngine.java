package com.chineseall.orm.storage;

import com.chineseall.orm.exception.ActiveRecordException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class CacheEngine implements ModelEngine {
    public Class getModelClass(){
        return null;
    }
    public Object fetch(Object[] key, boolean auto_create) throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public List<Object> fetchMulti(List<java.lang.Object[]> keys) throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public void save() throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public void delete() throws ActiveRecordException {
        throw new NotImplementedException();
    }
}
