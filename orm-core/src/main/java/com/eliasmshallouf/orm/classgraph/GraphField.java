package com.eliasmshallouf.orm.classgraph;

public class GraphField {
    private String fieldName;
    private String columnName;
    private Class<?> clazz;

    public GraphField(String fieldName, String columnName, Class<?> clazz) {
        this.fieldName = fieldName;
        this.columnName = columnName;
        this.clazz = clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
