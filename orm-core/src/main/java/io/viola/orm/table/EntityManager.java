package io.viola.orm.table;

import io.viola.orm.ConnectionManager;
import io.viola.orm.NativeConnectionHelper;
import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.columns.SetColumn;
import io.viola.orm.functions.aggergation.Count;
import io.viola.orm.helpers.LogicalStream;
import io.viola.orm.paging.Paging;
import io.viola.orm.query.Query;
import io.viola.orm.query.SelectQuery;

import java.util.List;

public class EntityManager<E, Id> {
    private final ConnectionManager connectionManager;
    private final EntityModel<E, Id> table;
    
    public EntityManager(ConnectionManager connectionManager, EntityModel<E, Id> table) {
        this.connectionManager = connectionManager;
        this.table = table;
    }

    public List<E> getAll() {
        return query().list();
    }

    public Paging<E> pages(int pageSize, boolean lazyFetch) {
        return pages(query().queryBuilder(), pageSize, lazyFetch);
    }

    public Paging<E> pages(String qry, int pageSize, boolean lazyFetch) {
        return new Paging<>(
            connectionManager,
            this.table.getClazz(),
            qry,
            pageSize,
            lazyFetch
        );
    }

    public long count() {
        return LogicalStream
            .of(connectionManager.tableCountRowsQuery(this.table.getTable()))
            .ifTrue(q -> q != null && !q.isEmpty())
            .thenReturn(q -> connectionManager.findOne(q, Long.class))
            .otherwise(q -> query().select(Count.all()).find(Long.class))
            .get();
    }

    public boolean contains(Id id) {
        return findById(id) != null;
    }

    public E findById(Id id) {
        return
            query()
                .where(table.getIdField().equal(ColumnInfo.valueOf(id)))
                .find();
    }

    @SafeVarargs
    public final void save(E... items) {
        for(E e : items) {
            try {
                NativeConnectionHelper.insert(this.connectionManager.getConnection(), this.table, e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public int deleteAll() {
        return delete(Query.noCondition());
    }

    public int deleteById(Id id) {
        return delete(table.getIdField().equal(ColumnInfo.valueOf(id)));
    }

    public int delete(Query where) {
        try {
            return NativeConnectionHelper.delete(this.connectionManager.getConnection(), this.table, () -> where);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> List<T> rawQuery(String qry, Class<T> clazz) {
        return connectionManager.rawQuery(qry, clazz);
    }

    public List<E> list(String qry) {
        return rawQuery(qry, table.getClazz());
    }

    public SelectQuery<E> query() {
        return new SelectQuery<E>(connectionManager).table(this.table);
    }

    public E findOne(String qry) {
        return connectionManager.findOne(qry, table.getClazz());
    }

    public int update(Query where, SetColumn<?> ...cols) {
        try {
            return NativeConnectionHelper.update(this.connectionManager.getConnection(), this.table, cols, () -> where);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int update(E e) {
        try {
            return NativeConnectionHelper.updateById(this.connectionManager.getConnection(), this.table, e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public int updateById(E e, Id id) {
        try {
            return NativeConnectionHelper.updateById(this.connectionManager.getConnection(), this.table, e, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public int saveOrUpdate(E e) {
        if(update(e) == 0) {
            save(e);
            return 0;
        }

        return 1;
    }

    public EntityManager<E, Id> withConnection(ConnectionManager connectionManager) {
        return new EntityManager<>(connectionManager, this.table);
    }
}
