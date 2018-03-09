package com.chineseall.orm.storage;

import com.chineseall.orm.RedisClient;

/**
 * Created by wangqiang on 2018/3/7.
 */
public class RedisNonemark extends Nonemark {
    private int expire_sec;

    public RedisNonemark(int expire_sec){
        this.expire_sec = expire_sec;
    }

    public void put(String general_key, int expire_sec){
        String key = gen_key(general_key);
        if(expire_sec<=0){
            expire_sec = this.expire_sec;
            RedisClient.getResource().setex(key, expire_sec,"");
        }
    }

    public boolean exists(String general_key){
        String key = gen_key(general_key);
        return RedisClient.getResource().exists(key);
    }

    public void remove(String  general_key){
        String key = gen_key(general_key);
        RedisClient.getResource().del(key);
    }

}
