package com.eliasmshallouf.orm.columns;

import com.eliasmshallouf.orm.helpers.Mapper;
import com.eliasmshallouf.orm.query.Query;
import com.eliasmshallouf.orm.table.TableColumns;

public class NumericColumn<N extends Number> extends ColumnInfo<N> {
    public NumericColumn(String table, String column) {
        super(table, column);
    }

    public NumericColumn(Class<?> table, String column) {
        super(table, column);
    }

    public NumericColumn(TableColumns<?> table, String column) {
        super(table, column);
    }

    public <O extends ColumnInfo<N>> NumericColumn<N> add(O i) {
        return new NumericColumn<>("", "(" + column() + " + " + i.column()+ ")");
    }

    public <O extends ColumnInfo<N>> NumericColumn<N> subtract(O i) {
        return new NumericColumn<>("", "(" + column() + " - " + i.column() + ")");
    }

    public <O extends ColumnInfo<N>> NumericColumn<N> divide(O i) {
        return new NumericColumn<>("", "(" + column() + " / " + i.column() + ")");
    }

    public <O extends ColumnInfo<N>> NumericColumn<N> multiple(O i) {
        return new NumericColumn<>("", "(" + column() + " * " + i.column() + ")");
    }

    public <O extends ColumnInfo<N>> Query biggerThan(O i) {
        return () -> column() + " > " + i.column();
    }

    public <O extends ColumnInfo<N>> Query biggerThanEquals(O i) {
        return () -> column() + " >= " + i.column();
    }

    public <O extends ColumnInfo<N>> Query lowerThan(O i) {
        return () -> column() + " < " + i.column();
    }

    public <O extends ColumnInfo<N>> Query lowerThanEquals(O i) {
        return () -> column() + " <= " + i.column();
    }

    public <O extends ColumnInfo<N>> Query between(O l, O h) {
        return () -> column() + namingActions.doKeywordChange(" between ") + l.column() + namingActions.doKeywordChange(" and ") + h.column();
    }

    public <T extends Number> NumericColumn<T> castTo(Class<T> clazz) {
        return new NumericColumn<>("", namingActions.doKeywordChange("cast") + "(" + column() + namingActions.doKeywordChange(" as ") + Mapper.mapToSqlType(clazz) + ")") { };
    }
}
