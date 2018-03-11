package com.chineseall.orm;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.chineseall.orm.annotations.Table;
import com.chineseall.orm.storage.CacheEngine;
import com.chineseall.orm.storage.MysqlObjectEngine;
import com.chineseall.orm.storage.MysqlValueEngine;
import com.chineseall.orm.storage.RedisEngine;
import com.chineseall.orm.annotations.ModelEngineType;
import org.reflections.Reflections;

public class ModelScanner {
    /**
     * 获取指定文件下面的RequestMapping方法保存在mapp中
     *
     * @param packageName
     * @return
     */
    public static void scanOrmModel(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(Table.class);
        try {
            // 存放url和ExecutorBean的对应关系
            for (Class<?> classz : classesList) {
                //得到该类下面的所有方法
                Table t = (Table)classz.getAnnotation(Table.class);
                Object engineObj= getEngine(t.engine(),classz,t.name(),t.deleteMark(),t.view(),t.column());
                Field engineField= classz.getField("model_engine");
                engineField.setAccessible(true);
                engineField.set(null, engineObj);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static<E> Object getEngine(ModelEngineType type, Class<E> modelClass, String table,String deleteMark, String view,String column){
        if(type==ModelEngineType.CACHE_MYSQL_OBJECT){
            return CacheEngine.getMysqlObjectCacheEngine(modelClass, table,  deleteMark,  view,null,null);
        }else if (type==ModelEngineType.CACHE_MYSQL_VALUE){
            return CacheEngine.getMysqlValueCacheEngine(modelClass, table,column,  deleteMark,  view,null,null);
        }else if (type==ModelEngineType.MYSQL_OBJECT){
            return new MysqlObjectEngine<E>(modelClass, table,deleteMark,  view);
        }else if (type==ModelEngineType.MYSQL_VALUE){
            return new MysqlValueEngine<E>(modelClass, table,column,deleteMark,  view);
        }else if (type==ModelEngineType.REDIS){
            return new RedisEngine<E>(modelClass, 0);
        }else{
            return null;
        }

    }
}