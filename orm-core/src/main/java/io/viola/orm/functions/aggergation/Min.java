package io.viola.orm.functions.aggergation;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;

public class Min<E> extends ColumnInfo<E> {
    public static <E> Min<E> of(ColumnInfo<E> col) {
        return new Min<>(col);
    }

    private Min(ColumnInfo<E> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("min") + "(%s)".formatted(col.column()));
    }
}
