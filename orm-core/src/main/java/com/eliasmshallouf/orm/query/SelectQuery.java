package com.eliasmshallouf.orm.query;

import com.eliasmshallouf.orm.columns.OrderColumn;
import com.eliasmshallouf.orm.logger.Logger;
import com.eliasmshallouf.orm.paging.Paging;
import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.table.EntityModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectQuery<T> {
    private final ConnectionManager manager;
    private final SubQuery<T> query = new SubQuery<>();
    private final List<UnionQuery<?>> unionQueries = new ArrayList<>();

    public SelectQuery(ConnectionManager m) {
        this.manager = m;
    }

    public SelectQuery<T> table(EntityModel<T, ?> table) {
        this.query.table(table);
        return this;
    }

    public SelectQuery<T> join(EntityModel<?, ?> another, Query on) {
        this.query.join(another, on);
        return this;
    }

    public SelectQuery<T> innerJoin(EntityModel<?, ?> another, Query on) {
        this.query.innerJoin(another, on);
        return this;
    }

    public SelectQuery<T> leftJoin(EntityModel<?, ?> another, Query on) {
        this.query.leftJoin(another, on);
        return this;
    }

    public SelectQuery<T> rightJoin(EntityModel<?, ?> another, Query on) {
        this.query.rightJoin(another, on);
        return this;
    }

    public SelectQuery<T> fullJoin(EntityModel<?, ?> another, Query on) {
        this.query.fullJoin(another, on);
        return this;
    }

    public SelectQuery<T> crossJoin(EntityModel<?, ?> another, Query on) {
        this.query.crossJoin(another, on);
        return this;
    }

    public SelectQuery<T> select(ColumnInfo<?>...cols) {
        this.query.select(cols);
        return this;
    }

    public SelectQuery<T> select(EntityModel<?, ?> entity) {
        this.query.select(entity);
        return this;
    }

    public SelectQuery<T> where(Query q) {
        this.query.where(q);
        return this;
    }

    public SelectQuery<T> groupBy(ColumnInfo<?> ...cols) {
        this.query.groupBy(cols);
        return this;
    }

    public SelectQuery<T> having(Query q) {
        this.query.having(q);
        return this;
    }

    public SelectQuery<T> orderBy(OrderColumn<?>...cols) {
        this.query.orderBy(cols);
        return this;
    }

    public SelectQuery<T> limit(long limit) {
        this.query.limit(limit);
        return this;
    }

    public SelectQuery<T> skip(long skip) {
        this.query.skip(skip);
        return this;
    }

    public SelectQuery<T> union(SelectQuery<?> other) {
        this.unionQueries.add(new UnionQuery<>(other, UnionQuery.UnionType.DEFAULT));
        return this;
    }

    public SelectQuery<T> unionAll(SelectQuery<?> other) {
        this.unionQueries.add(new UnionQuery<>(other, UnionQuery.UnionType.ALL));
        return this;
    }

    public String queryBuilder() {
        StringBuilder builder = new StringBuilder();

        if(unionQueries.isEmpty())
            builder.append(this.query.query());
        else
            builder
                .append("(")
                .append(this.query.query())
                .append(") ")
                .append(unionQueries.stream().map(
                    unionQuery -> new StringBuilder()
                        .append(
                            ConnectionManager.getNaming().doKeywordChange(
                                unionQuery.getType() == UnionQuery.UnionType.DEFAULT ? "UNION" : "UNION ALL"
                            )
                        )
                        .append(" (")
                        .append(unionQuery.getQuery().query.query())
                        .append(")")
                    ).collect(Collectors.joining(" "))
                );

        return builder.toString();
    }

    public <R> List<R> list(Class<R> resultClass) {
        String qry = queryBuilder();

        ConnectionManager.getLogger().printLog(
            "select query",
            qry,
            Logger.LogLevel.LOG
        );

        return this.manager.rawQuery(
            qry,
            resultClass
        );
    }

    public List<T> list() {
        return list(query.table.getClazz());
    }

    public <R> R find(Class<R> resultClass) {
        long tmp = query.limit;

        this.query.limit(1);

        List<R> list = list(resultClass);

        this.query.limit(tmp);

        return list.size() == 1 ? list.get(0) : null;
    }

    public T find() {
        return find(query.table.getClazz());
    }

    public <R> Paging<R> paging(int pageSize, boolean lazyFetch, Class<R> clazz) {
        return new Paging<>(
            manager,
            clazz,
            queryBuilder(),
            pageSize,
            lazyFetch
        );
    }

    public Paging<T> paging(int pageSize, boolean lazyFetch) {
        return paging(pageSize, lazyFetch, this.query.table.getClazz());
    }
}
