package io.viola.orm.functions.aggergation;

import io.viola.orm.columns.NumericColumn;
import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.table.EntityModel;

import java.util.Objects;

public class Count extends NumericColumn<Long> {
    public static Count all() {
        return new Count();
    }

    public static Count of(ColumnInfo<?> col) {
        if(Objects.equals(col.column(), "*"))
            return all();

        return new Count(col);
    }

    private Count(ColumnInfo<?> col) {
        super("", ConnectionManager.getNaming().doKeywordChange("count") + "(%s)".formatted(col.column()));
    }

    private Count() { this(ColumnInfo.all(EntityModel.defineEntity(""))); }
}
