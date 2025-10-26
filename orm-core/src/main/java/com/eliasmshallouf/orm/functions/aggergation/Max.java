package com.eliasmshallouf.orm.functions.aggergation;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;

public class Max<E> extends ColumnInfo<E> {
    public static <E> Max<E> of(ColumnInfo<E> col) {
        return new Max<>(col);
    }

    private Max(ColumnInfo<E> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("max") + "(%s)".formatted(col.column()));
    }
}
