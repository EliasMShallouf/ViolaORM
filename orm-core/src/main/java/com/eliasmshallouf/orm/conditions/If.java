package com.eliasmshallouf.orm.conditions;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.query.Query;

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
