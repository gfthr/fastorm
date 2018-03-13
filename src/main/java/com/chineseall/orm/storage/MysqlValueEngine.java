package com.chineseall.orm.storage;

import com.chineseall.orm.Model;
import com.chineseall.orm.exception.FastOrmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class MysqlValueEngine<T> extends AbstractMysqlEngine<T>{
    private String column;

    public MysqlValueEngine(Class<T> model_class, String table, String column,String delete_mark, String view){
        super(model_class,table,delete_mark,view);
        this.column = column;
    }

    public List<T> fetchMulti(List<Object[]> keys) throws FastOrmException {
        List<Map<String,Object>> data_dicts = this._fetch_rows_(keys);
        List<T> instances =new ArrayList<T>();
        for (Map<String,Object> data_dict:
                data_dicts) {
            if(data_dict!=null){
                Map<String,Object> iniVal=new HashMap<String,Object>();
                iniVal.put(this.column,data_dict.get(this.column));
                T instance=(T)this.model_class_create(this.getKeyValue(data_dict),iniVal);
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    protected String[] _get_column_names_for_select_(){
        return new String[]{this.column};
    }

    protected Object[] _get_columns_for_update_(Model model){
        String[] attr_names =new String[]{this.column};
        Object[] attr_values = this.__dump_values(model, attr_names);
        return new Object[]{attr_names,attr_values};
    }

    protected String[] _get_column_names_for_insert_(){
        return new String[]{this.column};
    }

    protected Object[] _get_column_values_for_insert_(Model model){
        String[] attr_names = this._get_column_names_for_insert_();
        return this.__dump_values(model, attr_names);
    }

    protected Object _row_to_value_( Map<String,Object> row_dict){
        return row_dict;
    }
}
