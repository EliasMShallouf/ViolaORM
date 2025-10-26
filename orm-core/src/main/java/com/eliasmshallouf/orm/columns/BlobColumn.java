package com.eliasmshallouf.orm.columns;

import com.eliasmshallouf.orm.table.TableColumns;

public class BlobColumn extends ColumnInfo<byte[]> {
    public BlobColumn(String table, String column) {
        super(table, column);
    }

    public BlobColumn(Class<?> table, String column) {
        super(table, column);
    }

    public BlobColumn(TableColumns<?> table, String column) {
        super(table, column);
    }
}
