package com.eliasmshallouf.orm.conditions;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.query.Query;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Switch<T> extends ColumnInfo<T> {
    private final List<Map.Entry<Query, ColumnInfo<T>>> cases;
    private ColumnInfo<T> otherwise;

    public Switch() {
        super(ColumnInfo.NO_TABLE, "");

        cases = new ArrayList<>();
        otherwise = valueOf(null);
    }

    public Switch<T> when(Query condition, ColumnInfo<T> valueToReturn) {
        this.cases.add(new AbstractMap.SimpleEntry<>(condition, valueToReturn));
        rebuildSql();
        return this;
    }

    public Switch<T> otherwise(ColumnInfo<T> valueToReturn) {
        this.otherwise = valueToReturn;
        rebuildSql();
        return this;
    }

    private void rebuildSql() {
        this.column =
            ConnectionManager.getNaming().doKeywordChange("case") + " " +
            cases.stream().map(e ->
                ConnectionManager.getNaming().doKeywordChange("when") + " " +
                e.getKey().sql() + " " +
                ConnectionManager.getNaming().doKeywordChange("then") + " " +
                e.getValue().column()
            ).collect(Collectors.joining(" ")) + " " +
            ConnectionManager.getNaming().doKeywordChange("else") + " " +
            otherwise.column() + " " +
            ConnectionManager.getNaming().doKeywordChange("end");
    }
}