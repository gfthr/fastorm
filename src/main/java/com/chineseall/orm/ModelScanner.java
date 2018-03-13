package com.chineseall.orm;

import com.chineseall.orm.annotations.ModelEngineType;
import com.chineseall.orm.annotations.Table;
import com.chineseall.orm.storage.*;
import org.reflections.Reflections;

import java.util.Set;

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
                ModelEngine engineObj= getEngine(t.engine(),classz,t.name(),t.deleteMark(),t.view(),t.column());
                ModelProxy.pushModelEngine(classz, engineObj);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static<E> ModelEngine getEngine(ModelEngineType type, Class<E> modelClass, String table, String deleteMark, String view, String column){
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