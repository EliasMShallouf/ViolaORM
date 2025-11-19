package io.viola.orm.columns;

import io.viola.orm.table.TableColumns;

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
