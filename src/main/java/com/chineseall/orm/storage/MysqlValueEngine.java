package com.chineseall.orm.storage;

import com.chineseall.orm.exception.ActiveRecordException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class MysqlValueEngine extends AbstractMysqlEngine{

    public MysqlValueEngine(Class model_class, String table, String delete_mark, String view){
        super(model_class,table,delete_mark,view);
    }

    public <E> E fetch(List<?> key, boolean auto_create) throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public <E> List<E> fetchMulti(List<?> keys) throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public <E> void save() throws ActiveRecordException {
        throw new NotImplementedException();
    }

    public <E> void delete() throws ActiveRecordException {
        throw new NotImplementedException();
    }
}
