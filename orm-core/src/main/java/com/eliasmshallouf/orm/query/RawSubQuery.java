package com.eliasmshallouf.orm.query;

import com.eliasmshallouf.orm.table.EntityModel;
import com.eliasmshallouf.orm.table.TableColumns;

public class RawSubQuery<T> extends EntityModel<T, Void> {
    private String qry;

    public RawSubQuery(String qry) {
        super(null);
        this.qry = qry;
    }

    public RawSubQuery<T> setQuery(String qry) {
        this.qry = qry;
        return this;
    }

    @Override
    public TableColumns<T> columns() {
        return new TableColumns<>(this) { };
    }

    @Override
    public String getTableName() {
        return "(" + this.qry + ")";
    }
}
