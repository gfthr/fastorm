package com.chineseall.orm;

import com.chineseall.orm.exception.ActiveRecordException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/10.
 */
public class ModelValue extends Model {

    public ModelValue(){

    }

    public Map<String,Object> demodelize(){
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        return this.dump(meta.get_column_names());
    }

    public static <E> E create(Object[] key, Object value) throws ActiveRecordException {
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        String valueColumnName = meta.get_column_names()[0];
        Map<String,Object> datadict= new HashMap<String,Object>();
        datadict.put(valueColumnName,value);
        return Model.create(key,datadict);

    }
}
