package com.chineseall.orm.exception;

/**
 * 数据验证异常类
 * @author stworthy
 */
public class ValidateException extends FastOrmException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidateException(String s){
        super(s);
    }
    
    public ValidateException(String s, Throwable root){
        super(s, root);
    }
    
    public ValidateException(Throwable root){
        super(root);
    }
}
