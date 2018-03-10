package com.chineseall.orm.storage;

import com.chineseall.orm.RedisClient;
import com.chineseall.orm.Setting;

import java.util.Random;

/**
 * Created by wangqiang on 2018/3/7.
 */
public class RedisNonemark extends Nonemark {
    private int expire_sec;

    public RedisNonemark(int expire_sec){

        this.expire_sec = _redis_expire_sec(expire_sec);
    }

    private int _redis_expire_sec(int expire_sec){
        if(expire_sec<=0){
            Random r = new Random();
            int sec = (int) Math.floor(Setting.nonemark_expired_seconds*(1+r.nextFloat()));
            return sec;
        }else{
            return expire_sec;
        }
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
