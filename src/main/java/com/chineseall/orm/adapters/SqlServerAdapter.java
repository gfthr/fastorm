package com.chineseall.orm.adapters;

public class SqlServerAdapter extends Adapter {
    public String getAdapterName(){
        return "sqlserver";
    }

    public String getLimitString(String sql, int limit, int offset){
        if (sql.toLowerCase().startsWith("query distinct")){
            return "query distinct top " + Integer.toString(limit+offset) + sql.substring("query distinct".length());
        }
        else{
            return "query top " + Integer.toString(limit+offset) + sql.substring("query".length());
        }
    }
    
    public boolean supportsLimitOffset(){
        return false;
    }
    
    public String getIdentitySelectString(){
        return "SELECT @@IDENTITY";
//        return "query scope_identity()";
    }
    
    public String getSequenceNextValString(String sequenceName){
        return null;
    }
}
