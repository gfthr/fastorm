package com.chineseall.orm;

import com.chineseall.orm.adapters.Adapter;
import com.chineseall.orm.connections.DataSourceConnectionProvider;
import com.chineseall.orm.utils.ConnectionsManager;

import javax.sql.DataSource;

public class FastOrmConnectionFactoryBean {
    private String dbName;
    private String adapterClass;
    private DataSource dataSource;

    public String getdbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
        init();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        init();
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
        init();
    }
    
    private void init(){
        if (dbName != null && dataSource != null){
            DataSourceConnectionProvider cp = new DataSourceConnectionProvider(dataSource);
            ConnectionsManager.putConnectionProvider(dbName, cp);
//            ActiveRecordBase.connections.put(domainBaseClass, cp);
        }
        if (dbName != null && adapterClass != null){
            try{
                Adapter adapter = (Adapter)Class.forName(adapterClass).newInstance();
                ConnectionsManager.putConnectionAdapter(dbName, adapter);
//                ActiveRecordBase.adapters.put(domainBaseClass, (Adapter)Class.forName(adapterClass).newInstance());
            }
            catch(Exception e){
                
            }
        }
    }
}
