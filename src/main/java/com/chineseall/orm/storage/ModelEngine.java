package com.chineseall.orm.storage;

import com.chineseall.orm.ModelMeta;
import com.chineseall.orm.exception.ActiveRecordException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public abstract class ModelEngine<T> {
    protected Class<T> model_class;

    public ModelEngine(Class<T> model_class) {
        this.model_class = model_class;
    }

    public Class<T> getModelClass() {
        return model_class;
    }

    public Object model_class_create(Object[] key, Object result_data) throws ActiveRecordException {

        return this.model_class_invoke_method(null, "create", new Class[]{key.getClass(), Map.class}, new Object[]{key, result_data});
    }

    public String model_class_gen_general_key(Object[] tuple_key) throws ActiveRecordException {
        return (String) this.model_class_invoke_method(null, "gen_general_key", new Class[]{tuple_key.getClass()}, new Object[]{tuple_key});
    }

    public Object model_class_invoke_method(Object object, String methodName, Class[] paras, Object[] para_values) throws ActiveRecordException {
        Object result = null;
        try {
            Method method = this.model_class.getMethod(methodName, paras);
            result = method.invoke(object, para_values);
        } catch (Exception ex) {
            throw new ActiveRecordException("model_class_invoke_method " + methodName + " error :" + ex.getMessage());
        }
        return result;
    }

    protected Object[] getKeyValue(Map<String,Object> data_dict){
        ModelMeta meta = ModelMeta.getModelMeta(this.model_class);
        Object[] key_values = new String[meta.idFields.length];
        for (int i=0;i<meta.idFields.length;i++){
            key_values[i] = data_dict.get(meta.idFields[i].getName());
        }
        return key_values;
    }


//    protected Object[] arrayChain(Object[] array1,Object[] array2){
//        Object[] array_new = new Object[array1.length + array2.length];
//        System.arraycopy(array1, 0, array_new, 0, array1.length);
//        System.arraycopy(array2, 0, array_new, array1.length, array2.length);
//        return array_new;
//    }

    public abstract T fetch(Object[] key, boolean auto_create) throws ActiveRecordException;

    public abstract List<T> fetchMulti(List<Object[]> keys) throws ActiveRecordException;

    public abstract void save(Object instance) throws ActiveRecordException;

    public abstract void delete(Object[] key_values) throws ActiveRecordException;
}
