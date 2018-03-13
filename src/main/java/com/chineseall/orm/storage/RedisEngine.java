package com.chineseall.orm.storage;

import com.alibaba.fastjson.JSON;
import com.chineseall.orm.Model;
import com.chineseall.orm.utils.RedisClient;
import com.chineseall.orm.utils.Setting;
import com.chineseall.orm.exception.FastOrmException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Created by wangqiang on 2018/3/5.
 */
public class RedisEngine<T> extends ModelEngine<T>{
    private int expire_sec;

    public RedisEngine(Class<T> model_class, int expire_sec){
        super(model_class);
        this.expire_sec = _redis_expire_sec(expire_sec);
    }

    private int _redis_expire_sec(int expire_sec){
        if(expire_sec==0){
            Random r = new Random();
            int sec = (int) Math.floor(Setting.redis_model_expired_seconds*(1+r.nextFloat()));
            return sec;
        }else if(expire_sec == -1){
            return expire_sec;
        }else{
            return expire_sec;
        }
    }


    public T fetch(Object[] key, boolean auto_create) throws FastOrmException {
        String store_key = model_class_gen_general_key(key);
        String store_data = RedisClient.getResource().get(store_key);

        if (!StringUtils.isEmpty(store_data)) {
            Map<String, Object> data = this.deserialize(store_data);
            return (T)model_class_create(key, data);
        }

        if (auto_create) {
            T instance = (T)model_class_create(key,null);
            this.save(instance);
            return instance;
        }
        return null;
    }

    public List<T> fetchMulti(List<Object[]> keys) throws FastOrmException {
        if(keys==null){
            return new ArrayList<T>();
        }
        String[] store_keys =new String[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            store_keys[i]=model_class_gen_general_key(keys.get(i));
        }
        List<String> store_data_list =  RedisClient.getResource().mget(store_keys);

        List<T> instances=new ArrayList<T>();
        for (String store_data:store_data_list
             ) {
            T instance =null;
            if(!StringUtils.isEmpty(store_data)){
                Map<String, Object> data = this.deserialize(store_data);
                Object[] tuple_key = getKeyValue(data);
                instance = (T)model_class_create(tuple_key, data);
            }
            instances.add(instance);
        }
        return instances;
    }

    public void save(Object instance) throws FastOrmException {
        if(!(instance instanceof Model))
            throw new FastOrmException("instance is not model");
        Model model =(Model)instance;
        Object[] tuple_key = model.tuple_key();

        // redis 不允许 None 的 key
        if(tuple_key==null){
            throw new FastOrmException("None key in save()");
        }

        String store_key = model.general_key();
        String store_data = this.serialize(model);

        if (this.expire_sec != -1){
            RedisClient.getResource().setex(store_key,this.expire_sec,store_data);
        }else{
            RedisClient.getResource().set(store_key, store_data);
        }
    }

    public void delete(Object[] key_values) throws FastOrmException {
        String store_key = model_class_gen_general_key(key_values);
        RedisClient.getResource().del(store_key);
    }

    public String serialize(Model model) throws FastOrmException {
        return JSON.toJSONString(model.demodelize());
    }

    public Map<String, Object> deserialize(String store_data) throws FastOrmException {
        return JSON.parseObject(store_data, Map.class);
    }
}
