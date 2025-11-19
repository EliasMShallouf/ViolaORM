package io.viola.orm.classgraph;

public class GraphField {
    private String fieldName;
    private String columnName;
    private Class<?> clazz;
    private boolean isPrimaryKey = false;
    private boolean isLob = false;

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

    public GraphField setIsLob(boolean lob) {
        isLob = lob;
        return this;
    }

    public GraphField setIsPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
        return this;
    }

    public boolean isLob() {
        return isLob;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
}
