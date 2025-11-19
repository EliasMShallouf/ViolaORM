package io.viola.orm.conditions;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Coalesce<T> extends ColumnInfo<T> {
    @SafeVarargs
    public Coalesce(ColumnInfo<T> ...nullableColumns) {
        super(
            ColumnInfo.NO_TABLE,
            ConnectionManager.getNaming().doKeywordChange("coalesce") + "(%s)".formatted(
                Arrays
                    .stream(nullableColumns)
                    .map(ColumnInfo::column)
                    .collect(Collectors.joining(", "))
            )
        );
    }
}
