package com.chineseall.orm.storage;

import com.chineseall.orm.Model;
import com.chineseall.orm.exception.ActiveRecordException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class MysqlObjectEngine<T> extends AbstractMysqlEngine<T>{

    public MysqlObjectEngine(Class<T> model_class, String table, String delete_mark, String view){
        super(model_class,table,delete_mark,view);
    }

//    public Class getModelClass(){
//        return this.model_class;
//    }

    public List<T> fetchMulti(List<Object[]> keys) throws ActiveRecordException{
        List<Map<String,Object>> data_dicts = this._fetch_rows_(keys);
        List<T> instances =new ArrayList<T>();
        for (Map<String,Object> data_dict:
                data_dicts) {
            if(data_dict!=null){
                T instance=(T)this.model_class_create(this.getKeyValue(data_dict),data_dict);
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    protected String[] _get_column_names_for_select_(){
        return this._column_names();
    }

    protected Object[] _get_columns_for_update_(Model model){
        String[] attr_names = new String[model.getModified_attrs().size()];
        for (int i = 0; i < model.getModified_attrs().size(); i++) {
            attr_names[i]=(String)model.getModified_attrs().iterator().next();
        }

        Object[] attr_values = this.__dump_values(model, attr_names);
        return new Object[]{attr_names,attr_values};
    }

    protected String[] _get_column_names_for_insert_(){
        return this._column_names();
    }

    protected Object[] _get_column_values_for_insert_(Model model){
        String[] attr_names = this._get_column_names_for_insert_();
        return this.__dump_values(model, attr_names);
    }

    protected Object _row_to_value_( Map<String,Object> row_dict){
        return row_dict;
    }




}
