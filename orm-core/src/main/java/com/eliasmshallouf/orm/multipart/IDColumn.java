package com.eliasmshallouf.orm.multipart;

import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.table.TableColumns;

public class IDColumn<T> extends ColumnInfo<T> {
    public IDColumn(String table, String column) {
        super(table, column);
    }

    public IDColumn(Class<?> table, String column) {
        super(table, column);
    }

    public IDColumn(TableColumns<?> table, String column) {
        super(table.getModel().getTable(), column); //to prevent add the id column to the column list
    }
}
