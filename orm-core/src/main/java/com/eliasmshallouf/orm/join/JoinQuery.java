package com.eliasmshallouf.orm.join;

import com.eliasmshallouf.orm.query.Query;

@FunctionalInterface
public interface JoinQuery<E1, E2> {
    Query join(E1 left, E2 right);
}
