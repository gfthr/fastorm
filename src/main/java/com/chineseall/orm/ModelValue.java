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
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        return this.dump(meta.get_column_names());
    }

    public static<T> T create(Object[] key, Object value) throws FastOrmException {
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        String valueColumnName = meta.get_column_names()[0];
        Map<String,Object> datadict= new HashMap<String,Object>();
        datadict.put(valueColumnName,value);
        return Model.create(key,datadict);

    }
}
