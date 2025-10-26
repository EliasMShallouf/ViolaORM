package com.eliasmshallouf.orm.functions;

import com.eliasmshallouf.orm.query.MonoSubQuery;
import com.eliasmshallouf.orm.columns.ColumnInfo;

public class All<T> extends Function<T, MonoSubQuery<?, T>> {
    public static <T> ColumnInfo<T> of(MonoSubQuery<?, T> q) {
        return new All<>(q).result();
    }

    private All(MonoSubQuery<?, T> q) {
        super("all", q);
    }
}
