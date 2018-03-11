package com.chineseall.orm;

import com.chineseall.orm.exception.ActiveRecordException;
import com.chineseall.orm.field.ColumnField;
import com.chineseall.orm.field.IdField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/10.
 */
public class ModelObject<T> extends Model<T> {

    public Map<String,Object> demodelize(){
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        return this.dump(meta.get_column_names());
    }


}
