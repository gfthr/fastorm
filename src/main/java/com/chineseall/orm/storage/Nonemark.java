package com.chineseall.orm.storage;

import com.chineseall.orm.Setting;
import sun.reflect.generics.reflectiveObjects.OrmNotImplementedException;
/**
 * Created by wangqiang on 2018/3/7.
 */
public abstract class Nonemark {

    public void put(String general_key, int expire_sec){
        throw new OrmNotImplementedException();
    }

    public boolean exists(String general_key){
        throw new OrmNotImplementedException();
    }

    public void remove(String  general_key){
        throw new OrmNotImplementedException();
    }


    public static String gen_key(String general_key){
        return String.format("%sNonemark|%s", Setting.REAL_CACHE_LOCAL_PREFIX,general_key);
    }
}