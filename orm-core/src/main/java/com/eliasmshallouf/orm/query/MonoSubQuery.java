package com.eliasmshallouf.orm.query;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.columns.OrderColumn;
import com.eliasmshallouf.orm.table.EntityModel;

public class MonoSubQuery<T, C> extends ColumnInfo<C> {
    private final SubQuery<T> query = new SubQuery<>();
    
    public MonoSubQuery(EntityModel<T, ?> table, ColumnInfo<C> target) {
        super("", "");

        this.query.limit(1);
        this.query.table(table);
        this.query.select(target);
    }

    public MonoSubQuery<T, C> join(EntityModel<?, ?> another, Query on) {
        this.query.join(another, on);
        return this;
    }

    public MonoSubQuery<T, C> innerJoin(EntityModel<?, ?> another, Query on) {
        this.query.innerJoin(another, on);
        return this;
    }

    public MonoSubQuery<T, C> leftJoin(EntityModel<?, ?> another, Query on) {
        this.query.leftJoin(another, on);
        return this;
    }

    public MonoSubQuery<T, C> rightJoin(EntityModel<?, ?> another, Query on) {
        this.query.rightJoin(another, on);
        return this;
    }

    public MonoSubQuery<T, C> fullJoin(EntityModel<?, ?> another, Query on) {
        this.query.fullJoin(another, on);
        return this;
    }

    public MonoSubQuery<T, C> crossJoin(EntityModel<?, ?> another, Query on) {
        this.query.crossJoin(another, on);
        return this;
    }

    public MonoSubQuery<T, C> where(Query q) {
        this.query.where(q);
        return this;
    }

    public MonoSubQuery<T, C> groupBy(ColumnInfo<?> ...cols) {
        this.query.groupBy(cols);
        return this;
    }

    public MonoSubQuery<T, C> having(Query q) {
        this.query.having(q);
        return this;
    }

    public MonoSubQuery<T, C> orderBy(OrderColumn<?>...cols) {
        this.query.orderBy(cols);
        return this;
    }

    public MonoSubQuery<T, C> skip(long skip) {
        this.query.skip(skip);
        return this;
    }

    @Override
    public String column() {
        return "(" + query.query() + ")";
    }

    public Query exists() {
        return () -> ConnectionManager.getNaming().doKeywordChange("exists ") + column();
    }

    public Query notExists() {
        return () -> ConnectionManager.getNaming().doKeywordChange("not exists ") + column();
    }
}
