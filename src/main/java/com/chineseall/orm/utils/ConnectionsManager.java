package com.chineseall.orm.utils;

import com.chineseall.orm.DatabaseConfReader;
import com.chineseall.orm.adapters.Adapter;
import com.chineseall.orm.connections.ConnectionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangqiang on 2018/3/14.
 * 数据库连接的持有者
 */
public class ConnectionsManager {
    //连接提供者
    private static Map<String,ConnectionProvider> connections = new HashMap<String,ConnectionProvider>();
    //数据库适配器（方言）
    private static Map<String,Adapter> adapters = new HashMap<String,Adapter>();

    static {
        try{
            DatabaseConfReader reader = new DatabaseConfReader();
            reader.init();
            connections = reader.getConnections();
            adapters = reader.getAdapters();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 取得数据库连接提供者
     * @param c 指定的数据库连接所绑定的类class
     * @return 连接提供者
     */
//    public static ConnectionProvider getConnectionProvider(Class<?> c) {
//        return connections.get(getBaseClassName(c));
//    }

    /**
     * 设置数据库连接提供者
     * @param dbName 域基类，由它登记数据库连接信息
     * @param cp 连接提供者
     */
    public static void putConnectionProvider(String dbName, ConnectionProvider cp){
        connections.put(dbName, cp);
    }

    /**
     * 取得数据库适配器
     * @param c 指定的数据库连接所绑定的类class
     * @return 数据库适配器
     */
//    public static Adapter getConnectionAdapter(Class<?> c){
//        return adapters.get(getBaseClassName(c));
//    }

    /**
     * 设置连接适配器
     * @param domainClassName 域基类，由它登记数据库连接信息
     * @param adapter 适配器
     */
    public static void putConnectionAdapter(String domainClassName, Adapter adapter){
        adapters.put(domainClassName, adapter);
    }

//    //TODO 这一段代码有疑问 ,获得c或者c的父类对应的 ConnectionProvider
//    private static String getBaseClassName(Class<?> c){
//        String className = c.getCanonicalName();
//        ConnectionProvider cp = connections.get(className);
//        while (cp == null){
//            c = c.getSuperclass();
//            if (c == null) {
//                return null;
//            }
//            className = c.getCanonicalName();
//            cp = connections.get(className);
//        }
//        return className;
//    }


    public static Map<String,ConnectionProvider> getConnections(){
        return connections;
    }
}
