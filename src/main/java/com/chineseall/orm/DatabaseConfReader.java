package com.chineseall.orm;

import com.chineseall.orm.adapters.Adapter;
import com.chineseall.orm.connections.ConnectionProvider;
import com.chineseall.orm.connections.DataSourceConnectionProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseConfReader {
    private Map<String,ConnectionProvider> connections = new HashMap<String,ConnectionProvider>();
    private Map<String,Adapter> adapters = new HashMap<String,Adapter>();
    
    public void init() throws Exception{
        Properties prop = new Properties();
        java.io.InputStream resource = getClass().getClassLoader().getResourceAsStream("database.properties");
        if (resource == null){
            resource = getClass().getClassLoader().getResourceAsStream("/database.properties");
        }
        if (resource != null){
            prop.load(resource);
            for(String bc: prop.getProperty("database").split(" ")){
//                Properties pp = new Properties();
//                pp.setProperty("driver_class", prop.getProperty(bc+".driver_class"));
//                pp.setProperty("url", prop.getProperty(bc+".url"));
//                pp.setProperty("username", prop.getProperty(bc+".username"));
//                pp.setProperty("password", prop.getProperty(bc+".password"));
//                pp.setProperty("pool_size", prop.getProperty(bc+".pool_size"));
//                String testTable = prop.getProperty(bc+".test_table");
//                if (testTable != null){
//                    pp.setProperty("test_table", testTable);
//                }


                HikariConfig config = new HikariConfig();
                config.setDriverClassName(prop.getProperty(bc+".driver_class"));
                config.setJdbcUrl(prop.getProperty(bc+".url"));
                config.setUsername(prop.getProperty(bc+".username"));
                config.setPassword(prop.getProperty(bc+".password"));
                config.setMaximumPoolSize(Integer.parseInt(prop.getProperty(bc+".pool_size")));

                //设置其他数据源配置信息
                HikariDataSource hikariDataSource = new HikariDataSource(config);

                DataSourceConnectionProvider cp = new DataSourceConnectionProvider(hikariDataSource);

                getConnections().put(bc, cp);
                
                String adapterClassName = prop.getProperty(bc+".adapter_class");
                if (adapterClassName != null){
                    Class<?> adapterClass = Class.forName(adapterClassName);
                    getAdapters().put(bc, (Adapter)adapterClass.newInstance());
                }
            }
        }
    }
    
    public Map<String, ConnectionProvider> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, ConnectionProvider> connections) {
        this.connections = connections;
    }

    public Map<String, Adapter> getAdapters() {
        return adapters;
    }

    public void setAdapters(Map<String, Adapter> adapters) {
        this.adapters = adapters;
    }
}
