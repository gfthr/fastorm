package com.chineseall.orm.storage;

import com.alibaba.fastjson.JSON;
import com.chineseall.orm.Model;
import com.chineseall.orm.RedisClient;
import com.chineseall.orm.exception.ActiveRecordException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/5.
 */
public class RedisEngine extends ModelEngine{
    private int expire_sec;

    public RedisEngine(Class model_class, int expire_sec){
        super(model_class);
        this.expire_sec = expire_sec;
    }

    public Object fetch(Object[] key, boolean auto_create) throws ActiveRecordException {
        String store_key = model_class_gen_general_key(key);
        String store_data = RedisClient.getResource().get(store_key);

        if (!StringUtils.isEmpty(store_data)) {
            Map<String, Object> data = this.deserialize(store_data);
            return model_class_create(key, data);
        }

        if (auto_create) {
            Object instance = model_class_create(key,null);
            this.save(instance);
            return instance;
        }
        return null;
    }

    public List<Object> fetchMulti(List<Object[]> keys) throws ActiveRecordException{
        if(keys==null){
            return new ArrayList<Object>();
        }
        String[] store_keys =new String[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            store_keys[i]=model_class_gen_general_key(keys.get(i));
        }
        List<String> store_data_list =  RedisClient.getResource().mget(store_keys);

        List<Object> instances=new ArrayList<Object>();
        for (String store_data:store_data_list
             ) {
            Object instance =null;
            if(!StringUtils.isEmpty(store_data)){
                Map<String, Object> data = this.deserialize(store_data);
                Object[] tuple_key = getKeyValue(data);
                instance = model_class_create(tuple_key, data);
            }
            instances.add(instance);
        }
        return instances;
    }

    public void save(Object instance) throws ActiveRecordException{
        if(!(instance instanceof Model))
            throw new ActiveRecordException("instance is not model");
        Model model =(Model)instance;
        Object[] tuple_key = model.tuple_key();

        // redis 不允许 None 的 key
        if(tuple_key==null){
            throw new ActiveRecordException("None key in save()");
        }

        String store_key = model.general_key();
        String store_data = this.serialize(model);

        if (this.expire_sec != -1){
            RedisClient.getResource().setex(store_key,this.expire_sec,store_data);
        }else{
            RedisClient.getResource().set(store_key, store_data);
        }
    }

    public void delete(Object[] key_values) throws ActiveRecordException {
        String store_key = model_class_gen_general_key(key_values);
        RedisClient.getResource().del(store_key);
    }

    public String serialize(Model model) throws ActiveRecordException{
        return JSON.toJSONString(model.demodelize());
    }

    public Map<String, Object> deserialize(String store_data) throws ActiveRecordException{
        return JSON.parseObject(store_data, Map.class);
    }
}
