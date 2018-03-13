package com.chineseall.orm;

import java.util.Map;

/**
 * Created by wangqiang on 2018/3/10.
 */
public class ModelObject<T> extends Model<T> {

    public Map<String,Object> demodelize(){
        ModelMeta meta = ModelMeta.getModelMeta(getModelEngine().getModelClass());
        return this.dump(meta.get_column_names());
    }


}
