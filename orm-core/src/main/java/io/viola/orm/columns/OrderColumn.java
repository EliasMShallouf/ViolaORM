package io.viola.orm.columns;

import io.viola.orm.ConnectionManager;

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
