package io.viola.orm.multipart;

import io.viola.orm.columns.Value;
import io.viola.orm.query.Query;
import io.viola.orm.NativeConnectionHelper;
import io.viola.orm.columns.ColumnInfo;
import io.viola.orm.table.EntityModel;
import io.viola.orm.table.TableColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultiFieldIDColumn<Obj, MP extends MultiFieldId<Obj>> extends IDColumn<MP> {
    private List<ColumnInfo> parts;
    private Class<MP> clazz;

    public MultiFieldIDColumn(String table, String ...column) {
        super(table, "");
        parts = new ArrayList<>(Arrays.stream(column).map(c -> ColumnInfo.defineColumn(table, c)).collect(Collectors.toList()));
    }

    public MultiFieldIDColumn(Class<?> table, String ...column) {
        this(NativeConnectionHelper.ClassParsingHelper._getTableName(table), column);
    }

    public MultiFieldIDColumn(TableColumns<?> table, String ...column) {
        this(table.getModel().getTable(), column);
    }

    public MultiFieldIDColumn(EntityModel<?, ?> model, ColumnInfo ...columns) {
        super(model.getTable(), ""); //to prevent add the id column to the column list
        parts = new ArrayList<>(List.of(columns));
    }

    public MultiFieldIDColumn<Obj, MP> withMainClassOf(Class<MP> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public Query equal(ColumnInfo<MP> e) {
        if (!(e instanceof Value<MP> val))
            throw new RuntimeException("MultiFieldIDColumn#equal must pass a parameter of type Value()");

        MP mp = val.getValue();
        Map<String, ?> values = mp.values();

        return
            parts
                .stream()
                .map(ci -> ci.equal(valueOf(values.get(ci.getColumn()))))
                .reduce(Query::and)
                .get();
    }

    public Query equal(Obj o) {
        return equal(valueOf(MP.from(this.clazz, o)));
    }
}
