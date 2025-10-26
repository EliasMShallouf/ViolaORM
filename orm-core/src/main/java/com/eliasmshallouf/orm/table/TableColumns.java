package com.eliasmshallouf.orm.table;

import com.eliasmshallouf.orm.columns.ColumnInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class TableColumns<T> {
    private EntityModel<T, ?> model;
    private List<ColumnInfo> columns = new ArrayList<>();

    public TableColumns(EntityModel<T, ?> model) {
        this.model = model;
    }

    public EntityModel<T, ?> getModel() {
        return model;
    }

    public <CI extends ColumnInfo<?>> void addColumn(CI c) {
        columns.add(c);
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }
}
