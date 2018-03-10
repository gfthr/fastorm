package com.chineseall.orm.storage;

import com.chineseall.orm.Setting;
import com.chineseall.orm.exception.OrmNotImplementedException;
/**
 * Created by wangqiang on 2018/3/7.
 */
public abstract class Nonemark {

    public abstract void put(String general_key, int expire_sec);

    public abstract boolean exists(String general_key);

    public abstract void remove(String  general_key);

    public static String gen_key(String general_key){
        return String.format("%sNonemark|%s", Setting.REAL_CACHE_LOCAL_PREFIX,general_key);
    }
}