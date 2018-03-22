package com.chineseall.orm.utils;

import com.chineseall.orm.connections.ConnectionProvider;
import com.chineseall.orm.exception.DataAccessException;
import com.chineseall.orm.exception.TransactionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbClient {
    //当前线程事务（多数据库）
    private static ThreadLocal<Map<String,Transaction>> currentTransactions = new ThreadLocal<Map<String,Transaction>>();
	private static Log log = LogFactory.getLog("DbClient");
    private ConnectionProvider cp;
    private String dbName;

    public DbClient(String dbName){
        ConnectionProvider cp =  ConnectionsManager.getConnections().get(dbName);
        this.cp = cp;
        this.dbName = dbName;
    }


    /**
     * 开始一个事务
     * @throws TransactionException
     */
    public void beginTransaction() throws TransactionException{
        Map<String,Transaction> map = currentTransactions.get();
        try{
            if (map == null){
                Connection con = cp.getConnection();
                Transaction transaction = new Transaction(con);
                transaction.beginTransaction();

                map = new HashMap<String,Transaction>();
                map.put(dbName, transaction);    //将当前事务保存起来

                currentTransactions.set(map);
            }
            else{
                Transaction transaction = map.get(dbName);
                if (transaction == null){
                    Connection con = cp.getConnection();
                    transaction = new Transaction(con);
                    transaction.beginTransaction();

                    map.put(dbName, transaction);    //将当前事务保存起来
                }
                else{
                    transaction.beginTransaction();     //增加事务级别
                }
            }
        }
        catch(SQLException e){
            throw new TransactionException(e);
        }
    }

    /**
     * 提交事务
     * @throws TransactionException
     */
    public  void commit() throws TransactionException{
        Map<String,Transaction> map = currentTransactions.get();
        Transaction transaction = map.get(dbName);
        transaction.commit();
        if (transaction.isFinished()){  //事务最终完成
            map.remove(dbName);
        }

        if (map.size() == 0){
            currentTransactions.remove();
        }
    }

    /**
     * 回滚事务
     * @throws TransactionException
     */
    public void rollback() throws TransactionException{
        Map<String,Transaction> map = currentTransactions.get();
        Transaction transaction = map.get(dbName);
        transaction.rollback();
        if (transaction.isFinished()){
            map.remove(dbName);
        }

        if (map.size() == 0){
            currentTransactions.remove();
        }
    }

    /**
     * 获取当前线程事务
     * @return 当前事务
     */
    public Transaction getCurrentTransaction(){
        Map<String,Transaction> map = currentTransactions.get();
        if (map == null){
            return null;
        }
        else{
            return map.get(dbName);
        }
    }


    public Connection getConnection() throws DataAccessException {
        Connection con;
        Transaction transaction = getCurrentTransaction();

        try{
            if (transaction == null){
                con = cp.getConnection();
            }
            else{
                con = transaction.getConnection();
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e);
        }
        return con;
    }

    public void closeConnection(Connection con) throws DataAccessException{
        if(con==null)
            return;
        Transaction transaction = getCurrentTransaction();
        try{
            if (transaction == null){
                cp.closeConnection(con);
            }
        }
        catch(SQLException e){
            throw new DataAccessException(e);
        }
    }

    public List<Map<String,Object>> query(String sql, Object[] args, int limit, int offset) throws DataAccessException{
        List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Object[] sqlParts = buildSql(sql, args);
        sql = sqlParts[0].toString();
        args = (Object[])sqlParts[1];
        String sqlInfo = sqlParts[2].toString();
        Connection conn = null;
        try{
        	long t1 = System.currentTimeMillis();
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for(int i=0; i<args.length; i++){
                    pstmt.setObject(i+1, args[i]);
                }
            }
            
            if (limit > 0){
                //设定最大记录数，如果驱动程序不支持则跳过
                try{
                    pstmt.setMaxRows(limit+offset);
                }
                catch(Exception e){}
            }
            rs = pstmt.executeQuery();
            
            int count = 0;
            while(count < offset && rs.next()){
                count ++;
            }
            
            ResultSetMetaData meta = rs.getMetaData();
            count = 0;
            while(rs.next() && (limit == 0 || count++ < limit)){
                Map<String,Object> item = new HashMap<String,Object>();
                for(int i=1; i<=meta.getColumnCount(); i++){
                    String name = meta.getColumnName(i).toLowerCase();
                    Object value = rs.getObject(i);
                    item.put(name, value);
                }
                data.add(item);
            }
            
            long t2 = System.currentTimeMillis();
            if (log.isDebugEnabled()){
            	log.debug((t2-t1)/1000.0 + "s " + sqlInfo);
            }
        }
        catch(SQLException e1){
    		throw new DataAccessException(sqlInfo, e1);
        }
        finally{
            try{
                if (rs != null){
                    rs.close();
                }
                if (pstmt != null){
                    pstmt.close();
                }
                if (conn != null){
                    closeConnection(conn);
                }
            }
            catch(SQLException e){
                throw new DataAccessException(e);
            }
        }
        
        return data;
    }

    public int count(String sql, Object[] args)throws DataAccessException{
        List<Map<String,Object>> result = this.query(sql, args,0,0);
        if(result!=null && result.size()>0 ){
            Map<String,Object> map = result.get(0);
            int raw_value =0 ;
            for (Map.Entry<String,Object> entry : map.entrySet()) {
                raw_value =Integer.parseInt((String) entry.getValue());
            }
            return raw_value;
        }else{
            return 0;
        }
    }

    public Object[] execute(String sql, Object[] args, boolean isInsert) throws DataAccessException{
        Object[] result= new Object[]{0,null};

        int updated = 0;
        Object[] sqlParts = buildSql(sql, args);
        sql = sqlParts[0].toString();
        args = (Object[])sqlParts[1];
        String sqlInfo = sqlParts[2].toString();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
        	long t1 = System.currentTimeMillis();
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for(int i=0; i<args.length; i++){
                    pstmt.setObject(i+1, args[i]);
                }
            }
            updated = pstmt.executeUpdate();

            result[0]=updated;
            if(isInsert){
                PreparedStatement insert_pstmt = conn.prepareStatement("select last_insert_id()");
                ResultSet rs = insert_pstmt.executeQuery();
                if (rs.next()){
                    result[1] = rs.getObject(1);
                }
            }
            
            long t2 = System.currentTimeMillis();
            if (log.isDebugEnabled()){
            	log.debug((t2-t1)/1000.0 + "s " + sqlInfo);
            }
        }
        catch(SQLException e){
            throw new DataAccessException(sqlInfo, e);
        }
        finally{
            try{
                if (pstmt != null){
                    pstmt.close();
                }
                if(conn!=null){
                    closeConnection(conn);
                }
            }
            catch(SQLException e){
                throw new DataAccessException(e);
            }
        }
        return result;
    }

    /**
     * 构建SQL语句，处理掉NULL值参数
     * @param sql 原始SQL语句
     * @param args 原始参数
     * @return 三个元素数组，分别是SQL是语句、参数、显示SQL语句
     */
    private Object[] buildSql(String sql, Object[] args){
    	Object[] result = new Object[3];
    	if (args == null){
    		result[0] = sql;
    		result[1] = args;
    		result[2] = sql;
    		return result;
    	}
    	
    	String newSql = "";
    	String showSql = "";
    	List<Object> tmpArgs = new ArrayList<Object>();
    	
    	String[] ss = (sql + " ").split("\\?");
    	for(int i=0; i<ss.length-1; i++){
    		Object arg = args[i];
    		if (arg == null) {
    			newSql += ss[i] + "null";
    			showSql += ss[i] + "null";
    		} else {
    			newSql += ss[i] + "?";
    			tmpArgs.add(arg);
    			if (arg instanceof String) {
    				showSql += ss[i] + "'" + arg + "'";
    			} else {
    				showSql += ss[i] + arg.toString();
    			}
    		}
    	}
    	newSql += ss[ss.length - 1];
    	showSql += ss[ss.length - 1];
    	
    	result[0] = newSql;
    	result[1] = tmpArgs.toArray();
    	result[2] = showSql;
    	
    	return result;
    }

}
