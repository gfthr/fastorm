package com.chineseall.orm.connections;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceConnectionProvider implements ConnectionProvider {
    private DataSource ds;
    
    public DataSourceConnectionProvider(DataSource ds){
        this.ds = ds;
    }
    
    public Connection getConnection() throws SQLException{
        return ds.getConnection();
    }
    
    public void closeConnection(Connection con) throws SQLException{
        con.close();
    }
    
    public void close() throws SQLException{
        
    }
}
