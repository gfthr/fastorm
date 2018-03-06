package com.chineseall.orm.storage;

import com.chineseall.orm.Model;
import com.chineseall.orm.exception.ActiveRecordException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class MysqlObjectEngine extends AbstractMysqlEngine{

    public MysqlObjectEngine(Class model_class, String table, String delete_mark, String view){
        super(model_class,table,delete_mark,view);
    }

    public Class getModelClass(){
        return this.model_class;
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

    public List<Object> fetchMulti(Object[][] keys) throws ActiveRecordException{
        List<Map<String,Object>> data_dicts = this._fetch_rows_(keys);
        List<Object> instances =new ArrayList<Object>();
        for (Map<String,Object> data_dict:
                data_dicts) {
            if(data_dict!=null){
                Object instance=this.model_class_create(this.getKeyValue(data_dict),data_dict);
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
            attr_names[i]=model.getModified_attrs().iterator().next();
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

    public static Object[] __dump_values(Model model, String[] attr_names){
        //按 attr_names 的顺序 dump 出 instance 的属性，并把 dict，list 转成字符串
        Map<String,Object> result_dict = model.dump(attr_names);

        Object[] result_list = new Object[attr_names.length];

        Object value=null;
        for (int i = 0; i < attr_names.length; i++) {
            String attr_name = attr_names[i];
            value = result_dict.get(attr_name);
            result_list[i]=value;
        }

//        if isinstance(value, (dict, list)):
//        value = ujson.dumps(value)
//        if isinstance(value, float):
//        value = "%.6f" % value


        return result_list;
    }


}
