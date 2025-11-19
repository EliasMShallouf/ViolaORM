package io.viola.orm.functions;

import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.query.MonoSubQuery;

public class Any<T> extends Function<T, MonoSubQuery<?, T>> {
    public static <T> ColumnInfo<T> of(MonoSubQuery<?, T> q) {
        return new Any<>(q).result();
    }

    private Any(MonoSubQuery<?, T> q) {
        super("any", q);
    }
}
