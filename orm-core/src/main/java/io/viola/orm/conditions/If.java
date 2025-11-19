package io.viola.orm.conditions;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.query.Query;

public class If<T> extends ColumnInfo<T> {
    public If(Query query, ColumnInfo<T> ifTrueReturn, ColumnInfo<T> elseReturn) {
        super(
            ColumnInfo.NO_TABLE,
            ConnectionManager.getNaming().doKeywordChange("if") + "(%s, %s, %s)".formatted(
                query.sql(),
                ifTrueReturn.column(),
                elseReturn.column()
            )
        );
    }
}
