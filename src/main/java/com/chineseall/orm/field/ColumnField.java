package com.chineseall.orm.field;

import java.lang.reflect.Field;

public class ColumnField {
    private String name;
    private Class<?> type;
    private Field field;
    private boolean isIdField;
    private String default_value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isIdField() {
        return isIdField;
    }

    public void setIdField(boolean idField) {
        isIdField = idField;
    }

    public String getDefault_value() {
        return default_value;
    }

    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }
}
