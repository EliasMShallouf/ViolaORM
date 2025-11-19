package io.viola.orm.functions;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Function<T, C extends ColumnInfo<T>> {
    @SafeVarargs
    public static <T> ColumnInfo<T> defineFunction(String func, ColumnInfo<T> ...params) {
        return new Function<>(func, params) {}.result();
    }

    private String func;
    private List<C> params;

    @SafeVarargs
    public Function(String func, C ...params) {
        this.func = func;
        this.params = new ArrayList<>(List.of(params));
    }

    public ColumnInfo<T> result() {
        return new ColumnInfo<>(
            ColumnInfo.NO_TABLE,
            ConnectionManager.getNaming().doKeywordChange(this.func) + "(%s)".formatted(
                this.params
                    .stream()
                    .map(ColumnInfo::column)
                    .collect(Collectors.joining(", "))
            )
        ) {};
    }
}
