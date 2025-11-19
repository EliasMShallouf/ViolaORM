package io.viola.orm.functions.aggergation;

import io.viola.orm.columns.NumericColumn;
import io.viola.orm.ConnectionManager;

public class Sum<N extends Number> extends NumericColumn<N> {
    public static <N extends Number> Sum<N> of(NumericColumn<N> col) {
        return new Sum<>(col);
    }

    private Sum(NumericColumn<N> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("sum") + "(%s)".formatted(col.column()));
    }
}
