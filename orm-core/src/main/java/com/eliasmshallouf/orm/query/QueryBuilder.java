package com.eliasmshallouf.orm.query;

@FunctionalInterface
public interface QueryBuilder<E> {
    Query query();
}