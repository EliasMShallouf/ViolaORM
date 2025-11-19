package io.viola.orm.conditions;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;

public class IfNull<T> extends ColumnInfo<T> {
    public IfNull(ColumnInfo<T> nullableColumn, ColumnInfo<T> elseReturn) {
        super(
            ColumnInfo.NO_TABLE,
            ConnectionManager.getNaming().doKeywordChange("ifnull") + "(%s, %s)".formatted(
                nullableColumn.column(),
                elseReturn.column()
            )
        );
    }
}
