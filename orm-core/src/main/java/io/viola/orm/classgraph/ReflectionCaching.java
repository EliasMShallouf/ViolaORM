package io.viola.orm.classgraph;

import io.viola.orm.annotations.Column;
import io.viola.orm.annotations.Entity;
import io.viola.orm.annotations.Id;
import io.viola.orm.annotations.Lob;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectionCaching {
    private final Map<Class<?>, ClassGraph<?>> cache;

    public static ReflectionCaching reflectionCaching;

    public static ReflectionCaching getCache() {
        if(reflectionCaching == null)
            reflectionCaching = new ReflectionCaching();

        return reflectionCaching;
    }

    private ReflectionCaching() { cache = new HashMap<>(); }

    public <E> ClassGraph<E> getGraph(Class<E> clazz) {
        ClassGraph<E> tmp = (ClassGraph<E>) this.cache.get(clazz);
        if(tmp == null) {
            tmp = analyzeClass(clazz);
            registerGraph(clazz, tmp);
        }

        return tmp;
    }

    public <E> void registerGraph(Class<E> clazz, ClassGraph<E> graph) {
        this.cache.put(clazz, graph);
    }

    private <E> ClassGraph<E> analyzeClass(Class<E> clazz) {
        ClassGraph<E> graph = new ClassGraph<>();

        graph.tableName = clazz.getSimpleName();

        if (clazz.isAnnotationPresent(Entity.class)) {
            String entityName = clazz.getAnnotation(Entity.class).name();

            if (!entityName.isEmpty())
                graph.tableName = entityName;
        }

        graph.entityClass = clazz;

        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(Lob.class)) {
                String fieldName = f.getName();
                if (f.isAnnotationPresent(Column.class)) {
                    String alias = f.getAnnotation(Column.class).name();
                    if (!alias.isEmpty())
                        fieldName = alias;
                }

                graph.addField(new GraphField(
                        f.getName(), fieldName, f.getType()
                ));
            }
        }

        return graph;
    }
}
