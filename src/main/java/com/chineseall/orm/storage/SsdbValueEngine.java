package com.chineseall.orm.storage;

import com.chineseall.orm.exception.ActiveRecordException;
import com.chineseall.orm.exception.OrmNotImplementedException;

import java.util.List;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class SsdbValueEngine extends AbstractSsdbEngine{
    public Object fetch(Object[] key, boolean auto_create) throws ActiveRecordException {
        throw new OrmNotImplementedException();
    }

    public List<Object> fetchMulti(List<java.lang.Object[]> keys) throws ActiveRecordException {
        throw new OrmNotImplementedException();
    }

    public void save(Object instance) throws ActiveRecordException {
        throw new OrmNotImplementedException();
    }

    public void delete(Object[] key_values) throws ActiveRecordException {
        throw new OrmNotImplementedException();
    }
}
