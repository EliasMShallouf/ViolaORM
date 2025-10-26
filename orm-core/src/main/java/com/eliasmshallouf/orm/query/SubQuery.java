package com.eliasmshallouf.orm.query;

import com.eliasmshallouf.orm.NativeConnectionHelper;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.columns.OrderColumn;
import com.eliasmshallouf.orm.helpers.ListHelper;
import com.eliasmshallouf.orm.join.Join;
import com.eliasmshallouf.orm.join.JoinType;
import com.eliasmshallouf.orm.table.EntityModel;
import com.eliasmshallouf.orm.table.TableColumns;

import java.util.Arrays;
import java.util.List;

public class SubQuery<T> extends EntityModel<T, Void> {
    protected EntityModel<T, ?> table;
    protected Join<?, ?> join;
    protected List<ColumnInfo> selectColumns;
    protected Query where;
    protected List<ColumnInfo> groupColumns;
    protected Query having;
    protected List<OrderColumn> orderColumns;
    protected long limit = -1;
    protected long skip = -1;
    private String sql = "";

    public SubQuery() {
        super(null);
    }

    public SubQuery<T> table(EntityModel<T, ?> table) {
        this.table = table;
        setClazz(table.getClazz());
        select(table);
        return rebuildQuery();
    }

    private SubQuery<T> join(EntityModel<?, ?> another, JoinType type, Query on) {
        if(join == null) {
            join = new Join<>(table, another, type, (l, r) -> on);
        } else {
            join = new Join<>(join, another, type, (l, r) -> on);
        }

        return rebuildQuery();
    }

    public SubQuery<T> join(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.DEFAULT_JOIN, on);
    }

    public SubQuery<T> innerJoin(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.INNER_JOIN, on);
    }

    public SubQuery<T> leftJoin(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.LEFT_OUTER_JOIN, on);
    }

    public SubQuery<T> rightJoin(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.RIGHT_OUTER_JOIN, on);
    }

    public SubQuery<T> fullJoin(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.FULL_OUTER_JOIN, on);
    }

    public SubQuery<T> crossJoin(EntityModel<?, ?> another, Query on) {
        return join(another, JoinType.CROSS_JOIN, on);
    }

    public SubQuery<T> select(ColumnInfo<?> ...cols) {
        this.selectColumns = Arrays.asList(cols);
        return rebuildQuery();
    }

    public SubQuery<T> select(EntityModel<?, ?> entity) {
        this.selectColumns = entity.columns().getColumns();
        return rebuildQuery();
    }

    public SubQuery<T> where(Query q) {
        this.where = q;
        return rebuildQuery();
    }

    public SubQuery<T> groupBy(ColumnInfo<?> ...cols) {
        this.groupColumns = Arrays.asList(cols);
        return rebuildQuery();
    }

    public SubQuery<T> having(Query q) {
        this.having = q;
        return rebuildQuery();
    }

    public SubQuery<T> orderBy(OrderColumn<?> ...cols) {
        this.orderColumns = Arrays.asList(cols);
        return rebuildQuery();
    }

    public SubQuery<T> limit(long limit) {
        this.limit = limit;
        return rebuildQuery();
    }

    public SubQuery<T> skip(long skip) {
        this.skip = skip;
        return rebuildQuery();
    }

    @Override
    public TableColumns<T> columns() {
        return new TableColumns<>(this) {
            @Override
            public <CI extends ColumnInfo<?>> void addColumn(CI c) { }

            @Override
            public List<ColumnInfo> getColumns() {
                return selectColumns;
            }
        };
    }

    public String query() {
        return sql;
    }
    
    private SubQuery<T> rebuildQuery() {
        this.sql = NativeConnectionHelper.buildSelectQuery(
            table,
            join,
            ListHelper.arrayOf(selectColumns, ColumnInfo.class),
            () -> where,
            ListHelper.arrayOf(groupColumns, ColumnInfo.class),
            () -> having,
            ListHelper.arrayOf(orderColumns, OrderColumn.class),
            limit,
            skip
        );
        
        return this;
    }

    @Override
    public String getTableName() {
        return "(" + query() + ")";
    }
}
