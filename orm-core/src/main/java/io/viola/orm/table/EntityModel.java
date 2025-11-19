package io.viola.orm.table;

import io.viola.orm.ConnectionManager;
import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.helpers.LogicalStream;
import io.viola.orm.NativeConnectionHelper;
import io.viola.orm.multipart.IDColumn;

public abstract class EntityModel<T, Id> {
    public static <E, I> EntityModel<E, I> defineEntity(String table, Class<E> clazz) {
        return new EntityModel<E, I>(clazz) {
            @Override
            public TableColumns<E> columns() {
                return new TableColumns<>(this) { };
            }
        }.setTable(table);
    }

    public static <I> EntityModel<Void, I> defineEntity(String table) {
        return defineEntity(table, null);
    }

    private Class<T> clazz;
    private String table;
    private String alias;
    private IDColumn<Id> idField;

    public EntityModel(Class<T> clazz) {
        this.clazz = clazz;
        this.table = clazz != null ? NativeConnectionHelper.ClassParsingHelper._getTableName(clazz) : "";
        this.alias = "";
    }

    public EntityModel(Class<T> clazz, String table) {
        this.clazz = clazz;
        this.table = table;
        this.alias = "";
    }

    public <E extends EntityModel<T, Id>> E setTable(String table) {
        this.table = table;
        return (E) this;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public <E extends EntityModel<T, Id>> E setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return (E) this;
    }

    public String getAlias() {
        return alias;
    }

    public <E extends EntityModel<T, Id>> E aliased(String alias) {
        this.alias = alias;
        refreshAlias();
        return (E) this;
    }

    public void refreshAlias() {
        this.columns().getColumns().forEach(columnInfo -> columnInfo.setTable(toString()));
    }

    public String getTable() {
        return table;
    }

    public abstract TableColumns<?> columns();

    public void setIdField(IDColumn<Id> idField) {
        this.idField = idField;
    }

    public IDColumn<Id> getIdField() {
        return idField;
    }

    @Override
    public String toString() {
        return alias.isEmpty() ? table : alias;
    }

    public String getTableName() {
        return ConnectionManager.getNaming().doChange(table);
    }

    public String sql() {
        return getTableName() + LogicalStream
            .of(alias)
            .ifTrue(a -> !a.isEmpty())
            .then(a -> ConnectionManager.getNaming().doKeywordChange(" as ") + ConnectionManager.getNaming().doChange(a))
            .otherwise(a -> "")
            .get();
    }

    public EntityManager<T, Id> manager(ConnectionManager manager) {
        return manager.createEntityManager(this);
    }

    public ColumnInfo<String> allColumns() {
        return ColumnInfo.all(this);
    }
}
