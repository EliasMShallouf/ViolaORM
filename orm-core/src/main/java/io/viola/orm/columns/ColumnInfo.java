package io.viola.orm.columns;

import io.viola.orm.ConnectionManager;
import io.viola.orm.conditions.IfNull;
import io.viola.orm.conditions.Switch;
import io.viola.orm.helpers.LogicalStream;
import io.viola.orm.naming.Naming;
import io.viola.orm.query.Query;
import io.viola.orm.table.TableColumns;
import io.viola.orm.NativeConnectionHelper;
import io.viola.orm.conditions.If;
import io.viola.orm.multipart.IDColumn;
import io.viola.orm.table.EntityModel;

import java.time.temporal.Temporal;

public abstract class ColumnInfo<E> {
    public static <E> ColumnInfo<E> defineColumn(EntityModel<?, ?> model, String column) {
        return new ColumnInfo<>(model.columns(), column) { };
    }

    public static <E> ColumnInfo<E> defineColumn(String table, String column) {
        return new ColumnInfo<>(table, column) { };
    }

    public static <E> ColumnInfo<E> raw(String sql) {
        return defineColumn(NO_TABLE, sql);
    }

    public static final String NO_TABLE = ""; //use to create functions, aggregation columns and operators like (+, - ...)
    protected final static Naming namingActions = ConnectionManager.getNaming();

    protected String table;
    protected String column;
    protected String alias;

    protected boolean distinct = false;

    public ColumnInfo(String table, String column) {
        this.table = table;
        this.column = column;
        this.alias = "";
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ColumnInfo(Class<?> table, String column) {
        this(NativeConnectionHelper.ClassParsingHelper._getTableName(table), column);
    }

    public ColumnInfo(TableColumns<?> table, String column) {
        this(table.getModel().toString(), column);
        table.addColumn(this);
    }

    public String getColumn() {
        return column;
    }

    public Query equal(ColumnInfo<E> e) {
        return () -> column() + " = " + e.column();
    }

    public Query notEqual(ColumnInfo<E> e) {
        return () -> column() + " != " + e.column();
    }

    public Query isNull() {
        return () -> column() + namingActions.doKeywordChange(" is null");
    }

    public Query isNotNull() {
        return () -> column() + namingActions.doKeywordChange(" is not null");
    }

    public <CI extends ColumnInfo<E>> Query in(CI ci) {
        return () -> column() + namingActions.doKeywordChange(" in") + " (" + ci.column() +")";
    }

    public <CI extends ColumnInfo<E>> Query notIn(CI ci) {
        return () -> column() + namingActions.doKeywordChange(" not in") + " (" + ci.column() +")";
    }

    public <CI extends ColumnInfo<E>> CI as(String alias) {
        this.alias = alias;
        return (CI) this;
    }

    public <CI extends ColumnInfo<E>> CI distinct() {
        this.distinct = true;
        return (CI) this;
    }

    public String column() {
        return LogicalStream
            .of(table)
            .ifTrue(t -> !t.isEmpty())
            .then(t -> namingActions.doChange(t) + "." + namingActions.doChange(alias.isEmpty() ? column : alias))
            .otherwise(t -> column)
            .get();
    }

    public SetColumn<E> setTo(ColumnInfo<E> value) {
        return new SetColumn<>() {
            @Override
            public ColumnInfo<E> column() {
                return ColumnInfo.this;
            }

            @Override
            public ColumnInfo<E> value() {
                return value;
            }
        };
    }

    private OrderColumn<E> order(OrderColumn.OrderBy order) {
        return new OrderColumn<E>() {
            @Override
            public ColumnInfo<E> column() {
                return ColumnInfo.this;
            }

            @Override
            public OrderBy type() {
                return order;
            }
        };
    }

    public OrderColumn<E> ascendingOrder() {
        return order(OrderColumn.OrderBy.ASC);
    }

    public OrderColumn<E> descendingOrder() {
        return order(OrderColumn.OrderBy.DESC);
    }

    public String groupNaming() {
        return LogicalStream
            .of(alias)
            .ifTrue(t -> !t.isEmpty())
            .thenReturn(a -> namingActions.doChange(alias))
            .otherwise(a ->
                LogicalStream
                .of(table)
                .ifTrue(t -> !t.isEmpty())
                .then(t -> namingActions.doChange(t) + "." + namingActions.doChange(column))
                .otherwise(t -> column)
                .get()
            ).get();
    }

    @Override
    public String toString() {
        return
            (distinct ? namingActions.doKeywordChange("DISTINCT ") : "") +
            LogicalStream
                .of(table)
                .ifTrue(t -> !t.isEmpty())
                .then(t -> namingActions.doChange(t) + "." + namingActions.doChange(column))
                .otherwise(t -> column)
                .get() +
            LogicalStream
                .of(alias)
                .ifTrue(a -> !a.isEmpty())
                .then(a -> namingActions.doKeywordChange(" as ") + namingActions.doChange(a))
                .otherwise(a -> "")
                .get();
    }

    public static <E> Value<E> valueOf(E e) {
        return new Value<>(e);
    }

    public <N extends Number> NumericColumn<N> asNumber() {
        return new NumericColumn<>("", column());
    }

    public <D extends Temporal> DateColumn<D> asDate() {
        return new DateColumn<>("", column());
    }

    public BlobColumn asBlob() {
        return new BlobColumn("", column());
    }

    public TextColumn asText() {
        return new TextColumn("", column());
    }

    public static ColumnInfo<String> all(Class<?> table) {
        return new ColumnInfo<>(table, "*") {};
    }

    public static ColumnInfo<String> all(Object table) {
        return new ColumnInfo<>(table.getClass(), "*") {};
    }

    public static ColumnInfo<String> all(EntityModel<?, ?> entity) {
        return new ColumnInfo<>(entity.toString(), "*") {};
    }

    public static <T> Switch<T> switchOf() {
        return new Switch<>();
    }

    public If<E> $if(Query q, ColumnInfo<E> otherwise) {
        return new If<>(q, this, otherwise);
    }

    public Switch<E> $switch() {
        return new Switch<E>().otherwise(this);
    }

    public IfNull<E> ifNullReturn(ColumnInfo<E> otherwise) {
        return new IfNull<>(this, otherwise);
    }

    public IDColumn<E> id() {
        return new IDColumn<>(table, column);
    }
}
