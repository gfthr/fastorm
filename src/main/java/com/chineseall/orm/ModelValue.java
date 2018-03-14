package com.chineseall.orm;

import com.chineseall.orm.exception.FastOrmException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/10.
 */
public class ModelValue<T> extends Model<T> {

    public ModelValue(){

    }

    public Map<String,Object> demodelize(){
        ModelMeta meta = ModelMeta.getModelMeta(getModelEngine().getModelClass());
        return this.dump(meta.get_column_names());
    }

    public T create(Object[] key, Object value) throws FastOrmException {
        Class<T> classz= getModelClass();
        ModelMeta meta = ModelMeta.getModelMeta(classz);
        String valueColumnName = meta.get_column_names()[0];
        Map<String,Object> datadict= new HashMap<String,Object>();
        datadict.put(valueColumnName,value);
        return super.create(key,datadict);

    }
}
