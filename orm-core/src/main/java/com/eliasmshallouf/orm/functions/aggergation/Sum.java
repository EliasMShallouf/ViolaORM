package com.eliasmshallouf.orm.functions.aggergation;

import com.eliasmshallouf.orm.columns.NumericColumn;
import com.eliasmshallouf.orm.ConnectionManager;

public class Sum<N extends Number> extends NumericColumn<N> {
    public static <N extends Number> Sum<N> of(NumericColumn<N> col) {
        return new Sum<>(col);
    }

    private Sum(NumericColumn<N> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("sum") + "(%s)".formatted(col.column()));
    }
}
