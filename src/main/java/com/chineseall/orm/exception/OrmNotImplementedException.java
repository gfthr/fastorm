package com.chineseall.orm.exception;

/**
 * Created by wangqiang on 2018/3/7.
 */
public class OrmNotImplementedException extends ActiveRecordException {

    private static final long serialVersionUID = 1L;

    public OrmNotImplementedException(){
        super("Orm Not Implemented method");
    }
}
