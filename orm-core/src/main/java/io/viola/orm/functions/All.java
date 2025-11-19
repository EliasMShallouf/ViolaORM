package io.viola.orm.functions;

import io.viola.orm.query.MonoSubQuery;
import io.viola.orm.columns.ColumnInfo;

public class All<T> extends Function<T, MonoSubQuery<?, T>> {
    public static <T> ColumnInfo<T> of(MonoSubQuery<?, T> q) {
        return new All<>(q).result();
    }

    private All(MonoSubQuery<?, T> q) {
        super("all", q);
    }
}
