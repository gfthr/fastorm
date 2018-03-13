package com.chineseall.orm.exception;

/**
 * 活动记录操作异常基类
 * @author stworthy
 *
 */
public class FastOrmException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FastOrmException(String s){
        super(s);
    }
    
    public FastOrmException(String s, Throwable root){
        super(s, root);
    }
    
    public FastOrmException(Throwable root){
        super(root);
    }
}
