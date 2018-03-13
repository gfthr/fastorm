package com.chineseall.orm.storage;

import com.chineseall.orm.ModelMeta;
import com.chineseall.orm.exception.FastOrmException;

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

    public Object model_class_create(Object[] key, Object result_data) throws FastOrmException {

        return this.model_class_invoke_method(null, "create", new Class[]{Class.class, key.getClass(), Map.class}, new Object[]{this.model_class, key, result_data});
    }

    public String model_class_gen_general_key(Object[] tuple_key) throws FastOrmException {
        return (String) this.model_class_invoke_method(null, "gen_general_key", new Class[]{Class.class, tuple_key.getClass()}, new Object[]{this.model_class, tuple_key});
    }

    public Object model_class_invoke_method(Object object, String methodName, Class[] paras, Object[] para_values) throws FastOrmException {
        Object result = null;
        try {
            Method method = this.model_class.getMethod(methodName, paras);
            result = method.invoke(object, para_values);
        } catch (Exception ex) {
            throw new FastOrmException("model_class_invoke_method " + methodName + " error :" + ex.getMessage());
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

    public abstract T fetch(Object[] key, boolean auto_create) throws FastOrmException;

    public abstract List<T> fetchMulti(List<Object[]> keys) throws FastOrmException;

    public abstract void save(Object instance) throws FastOrmException;

    public abstract void delete(Object[] key_values) throws FastOrmException;
}
