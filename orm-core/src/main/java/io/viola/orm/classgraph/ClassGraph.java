package io.viola.orm.classgraph;

import java.util.HashMap;
import java.util.Map;

public class ClassGraph<E> {
    public Class<E> entityClass;
    public String tableName;
    public Map<String, GraphField> fields = new HashMap<>();

    public ClassGraph<E> setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
        return this;
    }

    public ClassGraph<E> setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public ClassGraph<E> addField(GraphField field) {
        this.fields.put(field.getColumnName(), field);
        return this;
    }

    public GraphField getField(String column) {
        return fields.get(column);
    }
}
