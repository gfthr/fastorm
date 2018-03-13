package com.chineseall.orm;

import com.chineseall.orm.annotations.*;
import com.chineseall.orm.exception.FieldAccessException;
import com.chineseall.orm.field.ColumnField;
import com.chineseall.orm.field.IdField;

import java.lang.reflect.Field;
import java.util.*;

public class ModelMeta {
    private static Map<Class<?>,ModelMeta> metaCache;
    public String db;
    public String table;
    //    String id;
    public GeneratorType idGeneratorType;
    //    Class<?> idType;
//    String[] columns;
    public boolean autoCreatable;
    public IdField[] idFields;
    public ColumnField[] columnFields;

    public Set<String> idFieldsSet;
    public Set<String> columnSet;

    private String[] key_column_names; //key字段名称
    private String[] column_names;//不包括key字段的字段名称

    public ModelMeta(){
        idFieldsSet = new HashSet<String>();
        columnSet = new HashSet<String>();
    }

    static {
        metaCache=new HashMap<Class<?>,ModelMeta>();
    }

    public String[] get_key_column_names(){
        return key_column_names;
    }

    public String[] get_column_names(){
        //不包括 Key 的字段
        return column_names;
    }

    public static ModelMeta getModelMeta(Class<?> clasz) {
        if(metaCache.containsKey(clasz)){
            return metaCache.get(clasz);
        }else{
            ModelMeta modelMeta = new ModelMeta();
            Database dbAnnotation = clasz.getAnnotation(Database.class);
            if (dbAnnotation != null) {
                modelMeta.db = dbAnnotation.name();
            }

            Table t = clasz.getAnnotation(Table.class);
            if (t != null) {
                modelMeta.table = t.name();
                modelMeta.idGeneratorType = t.generate();
                modelMeta.autoCreatable = t.autoCreatable();
            }

            List<ColumnField> columnFields = new ArrayList<ColumnField>();
            List<IdField> idFields = new ArrayList<IdField>();


            for (Field f : clasz.getDeclaredFields()) {
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    IdField field = new IdField();
                    field.setName(f.getName());
                    field.setType(f.getType());
                    field.setField(f);
                    idFields.add(field);
//                    idFieldsSet.add(f.getName());
                    modelMeta.idFieldsSet.add(f.getName());
                }
                Column column = f.getAnnotation(Column.class);
                if (column != null) {
                    ColumnField field = new ColumnField();
                    field.setName(f.getName());
                    field.setType(f.getType());
                    field.setField(f);
                    field.setDefault_value(column.default_value());
                    if(modelMeta.idFieldsSet.contains(f.getName())){
                        field.setIdField(true);
                    }else{
                        field.setIdField(false);
                    }
                    columnFields.add(field);
                    modelMeta.columnSet.add(f.getName());
                }
            }
            modelMeta.columnFields = columnFields.toArray(new ColumnField[columnFields.size()]);
            modelMeta.idFields = idFields.toArray(new IdField[idFields.size()]);



            if (idFields.size() > 1) {
                modelMeta.idGeneratorType = GeneratorType.NONE;
            }

            modelMeta.key_column_names = new String[modelMeta.idFields.length];
            for (int i=0;i<modelMeta.idFields.length;i++){
                modelMeta.key_column_names[i] = modelMeta.idFields[i].getName();
            }

            List<String> list_column_names=new ArrayList<String>();
            for (int i=0;i<modelMeta.columnFields.length;i++){
                if(modelMeta.idFieldsSet.contains(modelMeta.columnFields[i].getName()))
                    continue;
                list_column_names.add(modelMeta.columnFields[i].getName());
            }
            modelMeta.column_names = list_column_names.toArray(new String[list_column_names.size()]);

            metaCache.put(clasz, modelMeta);
            return modelMeta;
        }
    }

    public static Object getFieldValue(Class<?> clasz, String field, Object obj) throws FieldAccessException {
        try {
            Field f = clasz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            String err = "get field " + field + " value error.";
            throw new FieldAccessException(err, e);
        }
    }

    public static void setFieldValue(Class<?> clasz, String field, Object obj, Object value) throws FieldAccessException {
        try {
            Field f = clasz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            String err = "assign field " + field + " width value " + value + " error.";
            throw new FieldAccessException(err, e);
        }
    }

    public static String getMethodName(String fieldName) {
        String s1 = fieldName.substring(0, 1);
        String s2 = fieldName.substring(1);
        return "get" + s1.toUpperCase() + s2;
    }

    public static String getFieldName(String methodName) {
        String s1 = methodName.substring(3, 4);
        String s2 = methodName.substring(4);
        return s1.toLowerCase() + s2;
    }
}
