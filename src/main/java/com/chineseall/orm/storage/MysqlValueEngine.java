package com.chineseall.orm.storage;

import com.chineseall.orm.Model;
import com.chineseall.orm.exception.ActiveRecordException;
import com.chineseall.orm.exception.OrmNotImplementedException;

import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class MysqlValueEngine extends AbstractMysqlEngine{

    public MysqlValueEngine(Class model_class, String table, String delete_mark, String view){
        super(model_class,table,delete_mark,view);
    }

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

    @Override
    protected String[] _get_column_names_for_select_(){
        throw new OrmNotImplementedException();
    }

    protected Object[] _get_columns_for_update_(Model model){
        throw new OrmNotImplementedException();
    }

    protected String[] _get_column_names_for_insert_(){
        throw new OrmNotImplementedException();
    }

    protected Object[] _get_column_values_for_insert_(Model model){
        throw new OrmNotImplementedException();
    }

    protected Object _row_to_value_( Map<String,Object> row_dict){
        throw new OrmNotImplementedException();
    }

    public static Object[] __dump_values(Model model, String[] attr_names){throw new OrmNotImplementedException();
    }
}
