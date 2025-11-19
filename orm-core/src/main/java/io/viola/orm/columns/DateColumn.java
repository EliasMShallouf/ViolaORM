package io.viola.orm.columns;

import io.viola.orm.query.Query;
import io.viola.orm.table.TableColumns;

import java.time.temporal.Temporal;

public class DateColumn<D extends Temporal> extends ColumnInfo<D> {
    public DateColumn(String table, String column) {
        super(table, column);
    }

    public DateColumn(Class<?> table, String column) {
        super(table, column);
    }

    public DateColumn(TableColumns<?> table, String column) {
        super(table, column);
    }

    public <CI extends ColumnInfo<D>> Query before(CI col) {
        return () -> column() + " < " + col.column();
    }

    public <CI extends ColumnInfo<D>> Query beforeOrEquals(CI col) {
        return () -> column() + " <= " + col.column();
    }

    public <CI extends ColumnInfo<D>> Query after(CI col) {
        return () -> column() + " > " + col.column();
    }

    public <CI extends ColumnInfo<D>> Query afterOrEquals(CI col) {
        return () -> column() + " >= " + col.column();
    }

    public <CI extends ColumnInfo<D>> Query between(CI low, CI high) {
        return () -> column() + namingActions.doKeywordChange(" between ") + low.column() + namingActions.doKeywordChange(" and ") + high.column();
    }
}
