package com.eliasmshallouf.orm.columns;

public interface SetColumn<T> {
    ColumnInfo<T> column();
    ColumnInfo<T> value();

    default String sql() {
        return column().column() + " = " + value().column();
    }
}
