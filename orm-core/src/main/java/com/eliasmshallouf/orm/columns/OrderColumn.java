package com.eliasmshallouf.orm.columns;

import com.eliasmshallouf.orm.ConnectionManager;

public interface OrderColumn<T> {
    enum OrderBy { ASC, DESC };

    ColumnInfo<T> column();
    OrderBy type();

    default String sql() {
        return
            (column().alias.isEmpty() ? column().column() : ConnectionManager.getNaming().doChange(column().alias))
            +
            " " + ConnectionManager.getNaming().doKeywordChange(type().name());
    }
}
