package com.chineseall.orm.storage;

import com.chineseall.orm.exception.ActiveRecordException;

import java.util.List;

/**
 * Created by wangqiang on 2018/3/5.
 */
public interface ModelEngine {

    public Class getModelClass();

    public Object fetch(Object[] key, boolean auto_create) throws ActiveRecordException ;

    public List<Object> fetchMulti(List<Object[]> keys) throws ActiveRecordException ;

    public void save() throws ActiveRecordException ;

    public void delete() throws ActiveRecordException ;
}
