package io.viola.orm.join;

import io.viola.orm.query.Query;

@FunctionalInterface
public interface JoinQuery<E1, E2> {
    Query join(E1 left, E2 right);
}
