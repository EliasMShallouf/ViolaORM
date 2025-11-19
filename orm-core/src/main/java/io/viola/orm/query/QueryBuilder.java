package io.viola.orm.query;

@FunctionalInterface
public interface QueryBuilder<E> {
    Query query();
}