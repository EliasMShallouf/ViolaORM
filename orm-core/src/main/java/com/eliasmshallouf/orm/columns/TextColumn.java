package com.eliasmshallouf.orm.columns;


import com.eliasmshallouf.orm.query.Query;
import com.eliasmshallouf.orm.table.TableColumns;

public class TextColumn extends ColumnInfo<String> {
    public TextColumn(String table, String column) {
        super(table, column);
    }

    public TextColumn(Class<?> table, String column) {
        super(table, column);
    }

    public TextColumn(TableColumns<?> table, String column) {
        super(table, column);
    }

    public <CI extends ColumnInfo<String>> Query like(CI c) {
        return () -> column() + namingActions.doKeywordChange(" like ") + c.column();
    }

    public Query like(String s) {
        return () -> column() + namingActions.doKeywordChange(" like '") + s + "'";
    }

    public Query startsWith(String prefix) {
        return like(prefix + "%");
    }

    public Query endsWith(String suffix) {
        return like("%" + suffix);
    }
}
