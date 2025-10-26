package com.eliasmshallouf.orm.functions;

import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.query.MonoSubQuery;

public class Any<T> extends Function<T, MonoSubQuery<?, T>> {
    public static <T> ColumnInfo<T> of(MonoSubQuery<?, T> q) {
        return new Any<>(q).result();
    }

    private Any(MonoSubQuery<?, T> q) {
        super("any", q);
    }
}
