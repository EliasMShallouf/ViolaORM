package com.eliasmshallouf.orm.functions.aggergation;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;

public class Min<E> extends ColumnInfo<E> {
    public static <E> Min<E> of(ColumnInfo<E> col) {
        return new Min<>(col);
    }

    private Min(ColumnInfo<E> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("min") + "(%s)".formatted(col.column()));
    }
}
