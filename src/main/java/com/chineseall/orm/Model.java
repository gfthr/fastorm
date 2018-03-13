package com.chineseall.orm;

import com.chineseall.orm.exception.FastOrmException;
import com.chineseall.orm.field.ColumnField;
import com.chineseall.orm.field.IdField;
import com.chineseall.orm.storage.ModelEngine;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by wangqiang on 2018/3/2.
 */


public abstract class Model<T> {
    private boolean modified = true;
    private boolean model_saved = false;
    private boolean isproxy = false;
    private Set<String> modified_attrs =new HashSet<String>();

    public static ModelEngine model_engine = null;

    public void markModified() {
        if (this.modified)
            return;
        this.modified = true;
    }

    public void markFlushed() {
        this.modified = false;
        modified_attrs.clear();
    }

    public boolean isModified() {
        return this.modified;
    }

    public boolean isModel_saved() {
        return model_saved;
    }

    public Set<String> getModified_attrs() {
        return modified_attrs;
    }

    /**
     * 是否是一个代理对象
     *
     * @return 是代理对象时返回true，否则返回false。
     */
    public boolean isProxy() {
        return this.isproxy;
    }

    public Object[] tuple_key(){
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
        Object[] key = new Object[meta.idFields.length];
        try {
            for (int i = 0; i <meta.idFields.length ; i++) {
                key[i] = meta.idFields[i].getField().get(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return key;
    }



    public String general_key() {
        return gen_general_key(this.tuple_key()) ;
    }

    public static String gen_general_key( Object[] tuple_key){
        //生成 general key，子类可以按需覆盖此方法
        String key_str = StringUtils.arrayToDelimitedString(tuple_key,"|");
        return String.format("%s%s|%s",Setting.REAL_CACHE_LOCAL_PREFIX, model_engine.getModelClass(),key_str);
    }

    public static<T> T fetch(Object[] key, boolean auto_create) throws FastOrmException {
        /*
        根据 key 获取对象，如果 key 为 None 或者 (None,)，返回 None。
        :param auto_create:
        在获取不到对象时是否根据 key 新建一个（当 auto_creatable=True 时）
        :rtype: Model
        */

        if(key==null || (key!=null&&key.length==0)){
            //不允许传空的 key，如果真有需要再去掉这限制
            throw new FastOrmException("empty key  for fetch");
        }

        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());

        if(auto_create && !meta.autoCreatable){
            throw new FastOrmException(model_engine.getModelClass().getName()+"  is not auto_creatable");
        }

        T instance = (T)model_engine.fetch(key, auto_create);

        if(instance!=null && instance instanceof Model){
            ((Model)instance).markFlushed();
            ((Model)instance).model_saved = true;
        }
        return instance;
    }

    public static <T> List<T> fetchMulti(List<Object[]> keys) throws FastOrmException {
        /*
        一次性获取多个对象。
        根据 keys 的顺序返回获取的对象，对象获取不到时为 None。
        :type keys: list | __generator
        :return: list[instance]
        :rtype: list[Model]
        */
        List<T> instances=new ArrayList<T>();
        if(keys==null || (keys!=null && keys.size()<=0)) {
            return instances;
        }
        instances = (List<T>)model_engine.fetchMulti(keys);
        for (T instance:
            instances) {
            if(instance!=null){
                ((Model)instance).markFlushed();
                ((Model)instance).model_saved = true;
            }
        }
        return instances;
    }

    public void save() throws FastOrmException {
        if (!this.isModified())
            return;

        model_engine.save(this);

        this.markFlushed();
        this.model_saved = true;
    }

    public void delete() throws FastOrmException {
        model_engine.delete(this.tuple_key());
    }

    public static <T> T create(Object[] key, Map<?, ?> iniValue) throws FastOrmException {

        // 1)创建代理类,解决属性变化的监听问题
        ModelProxy proxy = new ModelProxy();
        T obj = (T)proxy.getProxyObject(model_engine.getModelClass());
        if (obj instanceof Model) {
            ModelMeta.setFieldValue(Model.class, "isproxy", obj, true);
            ModelMeta.setFieldValue(Model.class, "model_saved", obj, false);
        }
        ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());

        // 2)将key赋值
        if (key != null && key.length >= 1) {
            int i = 0;
            try {
                for (IdField f : meta.idFields) {
                    f.getField().setAccessible(true);
                    f.getField().set(obj, ConvertUtil.castFromObject(key[i], f.getType()));
                    i++;
                }
            } catch (Exception ex) {
                throw new FastOrmException("create 时 ID与值不匹配:"+ex.getMessage());
            }
        }

        // 3)如果value 为空则将非key字段赋予默认值,否则用value对非key字段赋值
        if (iniValue != null) {
            try {
                for (ColumnField f : meta.columnFields) {
                    if(f.isIdField())
                        continue;
                    Object defaultValue = null;
                    Object value =  iniValue.get(f.getName());
                    if (value == null) {
                        defaultValue = f.getDefault_value();
                    } else {
                        defaultValue = value ;
                    }
                    if (defaultValue == null)
                        continue;
                    f.getField().setAccessible(true);
                    f.getField().set(obj, ConvertUtil.castFromObject(defaultValue, f.getType()));
                }
            } catch (IllegalAccessException ex) {
                throw new FastOrmException("IllegalAccessException castFromObject error");
            }
        }
        return obj;
    }

    public abstract Map<String,Object> demodelize();


    public Map<String,Object> dump(String[] attrs){
        //指定属性导出成原始类型或 dict、list。
        Map<String,Object> result_dict = new HashMap<String,Object>();
        try {
            ModelMeta meta = ModelMeta.getModelMeta(model_engine.getModelClass());
            for (String attr:
                    attrs) {
                result_dict.put(attr,meta.getFieldValue(model_engine.getModelClass(),attr,this));
            }
        }catch (Exception e){
            e.printStackTrace();
            //("meta.getFieldValue error:"+e.getMessage());
        }
        return result_dict;
    }

}
