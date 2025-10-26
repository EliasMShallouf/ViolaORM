package com.eliasmshallouf.orm.functions;

import com.eliasmshallouf.orm.columns.ColumnInfo;

public class Concat extends Function<String, ColumnInfo<String>> {
    @SafeVarargs
    public static ColumnInfo<String> of(ColumnInfo<String> ...cols) {
        return new Concat(cols).result();
    }

    private Concat(ColumnInfo<String> ...cols) {
        super("concat", cols);
    }
}
