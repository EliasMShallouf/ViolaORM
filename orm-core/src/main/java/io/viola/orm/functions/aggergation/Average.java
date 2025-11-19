package io.viola.orm.functions.aggergation;

import io.viola.orm.columns.NumericColumn;
import io.viola.orm.ConnectionManager;

public class Average<N extends Number> extends NumericColumn<N> {
    public static <N extends Number> Average<N> of(NumericColumn<N> col) {
        return new Average<>(col);
    }

    private Average(NumericColumn<N> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("avg") + "(%s)".formatted(col.column()));
    }
}
