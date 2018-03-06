package com.chineseall.orm.validators;

import com.chineseall.orm.ActiveRecordBase;
import com.chineseall.orm.annotations.Unique;
import com.chineseall.orm.exception.ValidateException;

/**
 * 数据创建唯一性验证类
 * @author stworthy
 */
public class UniqueCreateValidator extends AbstractValidator<Unique> {
    private Class<?> c;
    private String tableName;
    private String fieldName;
    
    public UniqueCreateValidator(Class<?> c, String tableName, String fieldName, Unique parameters){
        super.init(parameters);
        this.c = c;
        this.tableName = tableName;
        this.fieldName = fieldName;
    }
    
    public boolean validate(Object value) throws ValidateException{
        try{
            String sql = "select count(*) from "+tableName+" where "+fieldName+"=?";
            Object[] args = new Object[]{value};
            Object count = ActiveRecordBase.executeScalar(c, sql, args);
            if (Integer.parseInt(count.toString()) > 0){
                return false;
            }
            else{
                return true;
            }
        }
        catch(Exception e){
            throw new ValidateException(e);
        }
    }
}
