package com.chineseall.orm;

import com.chineseall.orm.annotations.Column;
import com.chineseall.orm.annotations.GeneratorType;
import com.chineseall.orm.annotations.Id;
import com.chineseall.orm.annotations.Table;
import com.chineseall.orm.exception.FieldAccessException;
import com.chineseall.orm.field.ColumnField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OrmInfo {
    String table;
    String id;
    GeneratorType idGeneratorType;
    Class<?> idType;
//    String[] columns;
    ColumnField[] columnFields;

    // TODO 需要加缓存
    public static OrmInfo getOrmInfo(Class<?> clasz){
        OrmInfo orm = new OrmInfo();
        
        Table t = clasz.getAnnotation(Table.class);
        if (t != null) {
        	orm.table = t.name();
        }
        
        List<ColumnField> columnFields = new ArrayList<ColumnField>();

        for (Field f: clasz.getDeclaredFields()){
            Id id = f.getAnnotation(Id.class);
            if (id != null){
                orm.id = f.getName();
                orm.idGeneratorType = id.generate();
                orm.idType = f.getType();
            }
            Column column = f.getAnnotation(Column.class);
            if (column != null){
                ColumnField field = new ColumnField();
                field.setName(f.getName());
                field.setType(f.getType());
                columnFields.add(field);
            }
        }
        
        orm.columnFields = columnFields.toArray(new ColumnField[columnFields.size()]);
        return orm;
    }
    
    public static Object getFieldValue(Class<?> clasz, String field, Object obj) throws FieldAccessException{
        try{
            Field f = clasz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        }
        catch(Exception e){
        	String err = "get field " + field + " value error.";
            throw new FieldAccessException(err, e);
        }
    }
    
    public static void setFieldValue(Class<?> clasz, String field, Object obj, Object value) throws FieldAccessException{
        try{
            Field f = clasz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, value);
        }
        catch(Exception e){
        	String err = "assign field " + field + " width value " + value + " error.";
            throw new FieldAccessException(err, e);
        }
    }
    
    public static String getMethodName(String fieldName){
        String s1 = fieldName.substring(0,1);
        String s2 = fieldName.substring(1);
        return "get" + s1.toUpperCase() + s2;
    }
    
    public static String getFieldName(String methodName){
        String s1 = methodName.substring(3,4);
        String s2 = methodName.substring(4);
        return s1.toLowerCase()+s2;
    }
}
