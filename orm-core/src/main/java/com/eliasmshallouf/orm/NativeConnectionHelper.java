package com.eliasmshallouf.orm;

import com.eliasmshallouf.orm.annotations.Column;
import com.eliasmshallouf.orm.annotations.Entity;
import com.eliasmshallouf.orm.annotations.Id;
import com.eliasmshallouf.orm.annotations.Lob;
import com.eliasmshallouf.orm.columns.ColumnInfo;
import com.eliasmshallouf.orm.columns.OrderColumn;
import com.eliasmshallouf.orm.columns.SetColumn;
import com.eliasmshallouf.orm.exceptions.PrimaryKeyNotFoundException;
import com.eliasmshallouf.orm.helpers.LogicalStream;
import com.eliasmshallouf.orm.helpers.StringHelper;
import com.eliasmshallouf.orm.join.Join;
import com.eliasmshallouf.orm.logger.Logger;
import com.eliasmshallouf.orm.multipart.MultiFieldIDColumn;
import com.eliasmshallouf.orm.naming.Naming;
import com.eliasmshallouf.orm.query.QueryBuilder;
import com.eliasmshallouf.orm.table.EntityModel;
import com.eliasmshallouf.orm.helpers.ClassHelper;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NativeConnectionHelper {
    public static class ClassParsingHelper {
        public static String _getTableName(Class<?> clazz) {
            if(clazz == null)
                return "";

            String table = clazz.getSimpleName();

            if (clazz.isAnnotationPresent(Entity.class)) {
                String entityName = clazz.getAnnotation(Entity.class).name();
                if (!entityName.isEmpty())
                    table = entityName;
            }
            
            return table;
        }

        public static String getTableName(Class<?> clazz) {
            return ConnectionManager.getNaming().doChange(_getTableName(clazz));
        }

        public static String getEntityPrimaryKeyName(Class<?> clazz) {
            String idField = "";
            
            for(Field f : clazz.getDeclaredFields()) {
                if(f.isAnnotationPresent(Id.class)) {
                    idField = f.getName();
                    break;
                }
            }
            
            if(idField.isEmpty())
                throw new PrimaryKeyNotFoundException(clazz);
            
            return ConnectionManager.getNaming().doChange(idField);
        }

        public static <E, T> T getEntityPrimaryKeyValue(E e) {
            try {
                Class<E> clazz = (Class<E>) e.getClass();

                for (Field f : clazz.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Id.class)) {
                        f.setAccessible(true);
                        return (T) f.get(e);
                    }
                }

                throw new PrimaryKeyNotFoundException(clazz);
            } catch (IllegalAccessException illegalAccessException) {
                throw new RuntimeException(illegalAccessException);
            }
        }
    }
    
    public static Connection createConnection(
        String driver,
        String url,
        String user,
        String password
    ) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(
            url,
            user,
            password
        );
    }

    private static boolean isResultSetHaveColumn(ResultSet resultSet, String column) {
        try {
            return resultSet.findColumn(column) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static Object value(String fieldName, Class<?> clazz, ResultSet resultSet) throws SQLException {
        if(!isResultSetHaveColumn(resultSet, fieldName))
            return null;

        if (clazz.equals(Integer.class)) {
            return resultSet.getInt(fieldName);
        } else if(clazz.equals(Double.class)) {
            return resultSet.getDouble(fieldName);
        } else if(clazz.equals(Long.class)) {
            return resultSet.getLong(fieldName);
        } else if(clazz.equals(Boolean.class)) {
            return resultSet.getBoolean(fieldName);
        } else if(clazz.equals(Date.class)) {
            return resultSet.getDate(fieldName);
        } else if(clazz.equals(LocalDate.class)) {
            return resultSet.getDate(fieldName).toLocalDate();
        } else if(clazz.equals(LocalDateTime.class)) {
            return LocalDateTime.from(resultSet.getDate(fieldName).toInstant());
        } else if(clazz.equals(LocalTime.class)) {
            return LocalTime.from(resultSet.getDate(fieldName).toInstant());
        } else if(clazz.equals(Float.class)) {
            return resultSet.getFloat(fieldName);
        } else if(clazz.equals(String.class)) {
            return resultSet.getString(fieldName);
        } else if (clazz.equals(Byte.class)) {
            return resultSet.getByte(fieldName);
        } else if (clazz.equals(byte[].class)) {
            return resultSet.getBytes(fieldName);
        }

        return resultSet.getObject(fieldName);
    }

    private static byte[] getBlob(InputStream is) {
        if(is == null) return null;

        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes, 0, bytes.length);
            return bytes;
        } catch(Exception e) {
            return null;
        }
    }

    private static Object blob(String fieldName, Class<?> clazz, ResultSet resultSet) throws SQLException {
        if(!isResultSetHaveColumn(resultSet, fieldName))
            return null;

        byte[] arr = getBlob(resultSet.getBinaryStream(fieldName));

        if(arr == null)
            return null;

        if(clazz.equals(String.class))
            return new String(arr);

        return arr;
    }

    private static <E> E createFromResultSet(ResultSet resultSet, Class<E> clazz)
            throws IllegalAccessException, SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        if(ClassHelper.isPrimitive(clazz))
            return (E) value(metaData.getColumnName(1), clazz, resultSet);

        if(clazz.equals(Object[].class)) {
            Object[] res = new Object[resultSet.getMetaData().getColumnCount()];

            for(int i = 0 ; i < res.length ; i++)
                res[i] = value(metaData.getColumnName(i + 1), Object.class, resultSet);

            return (E) res;
        }

        if (clazz.equals(Map.class) || ClassHelper.isImplements(clazz, Map.class)) {
            HashMap<String, Object> map = new HashMap<>();

            for(int i = 0 ; i < metaData.getColumnCount() ; i++)
                map.put(
                    metaData.getColumnName(i + 1),
                    value(metaData.getColumnName(i + 1), Object.class, resultSet)
                );

            return (E) map;
        }

        E e = ClassHelper.createObject(clazz);

        for(Field f : clazz.getDeclaredFields()) {
            if(f.isAnnotationPresent(Column.class) || f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(Lob.class)) {
                String fieldName = f.getName();
                if(f.isAnnotationPresent(Column.class)) {
                    String alias = f.getAnnotation(Column.class).name();
                    if(!alias.isEmpty())
                        fieldName = alias;
                }

                f.setAccessible(true);

                if(f.isAnnotationPresent(Lob.class))
                    f.set(e, blob(f.getName(), f.getType(), resultSet));
                else
                    f.set(e, value(fieldName, f.getType(), resultSet));
            }
        }

        return e;
    }

    public static <E> E fetchOne(ResultSet resultSet, Class<E> clazz)
            throws IllegalAccessException, SQLException {
        if(resultSet.next()) {
            return createFromResultSet(resultSet, clazz);
        }
        return null;
    }

    public static <E> List<E> fetch(ResultSet resultSet, Class<E> clazz)
            throws IllegalAccessException, SQLException {
        List<E> list = new ArrayList<>();

        while (resultSet.next()) {
            list.add(createFromResultSet(resultSet, clazz));
        }

        return list;
    }

    public static <E> int insert(Connection connection, EntityModel<E, ?> model, E e) {
        try {
            Class<?> clazz = model.getClazz();
            String table = ConnectionManager.getNaming().doChange(model.getTable());

            List<String> cols = new ArrayList<>();
            List values = new ArrayList();
            List<Class<?>> classes = new ArrayList<>();

            for (Field f : clazz.getDeclaredFields()) {
                if (
                    f.isAnnotationPresent(Id.class) ||
                    f.isAnnotationPresent(Column.class) ||
                    f.isAnnotationPresent(Lob.class)
                ) {
                    String fieldName = LogicalStream
                        .of(f.getAnnotation(Column.class))
                        .ifTrue(c -> c != null && !c.name().isEmpty())
                        .thenReturn(Column::name)
                        .otherwise(c -> f.getName())
                        .get();

                    f.setAccessible(true);
                    Object val = f.get(e);

                    if (
                        !f.isAnnotationPresent(Id.class) ||
                        (f.isAnnotationPresent(Id.class) && val != null)
                    ) {
                        cols.add(fieldName);
                        values.add(val);
                        classes.add(f.getType());
                    }
                }
            }

            cols = cols.stream().map(c -> ConnectionManager.getNaming().doChange(c)).collect(Collectors.toList());

            String qry = (
                    ConnectionManager.getNaming().doKeywordChange("INSERT INTO") +
                            " %s(%s) " +
                            ConnectionManager.getNaming().doKeywordChange("VALUES") +
                            " (%s);"
            ).formatted(
                    table,
                    String.join(",", cols),
                    IntStream.range(0, cols.size()).mapToObj(i -> "?").collect(Collectors.joining(","))
            );

            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            for (int i = 0; i < cols.size(); i++) {
                preparedStatement.setObject(
                        i + 1,
                        values.get(i)
                );
            }

            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "insert query",
                    StringHelper.str(preparedStatement),
                    Logger.LogLevel.LOG
                );

            return preparedStatement.executeUpdate();
        } catch (Exception ex) {
            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "error in insert query on table " + model.getTable(),
                    ex.toString(),
                    Logger.LogLevel.ERROR
                );
            return -1;
        }
    }

    public static <E, Id> int updateById(Connection connection, EntityModel<E, Id> model, E e) {
        return updateById(
            connection,
            model,
            e,
            (model.getIdField() instanceof MultiFieldIDColumn<?, ?>)
                ? null
                : ClassParsingHelper.getEntityPrimaryKeyValue(e)
        );
    }

    public static <E, Id> int updateById(Connection connection, EntityModel<E, Id> model, E e, Id id) {
        try {
            Class<?> clazz = e.getClass();
            String table = ConnectionManager.getNaming().doChange(model.getTable());

            List<String> cols = new ArrayList<>();
            List values = new ArrayList();
            List<Class<?>> classes = new ArrayList<>();

            for (Field f : clazz.getDeclaredFields()) {
                if (
                    f.isAnnotationPresent(com.eliasmshallouf.orm.annotations.Id.class) ||
                    f.isAnnotationPresent(Column.class) ||
                    f.isAnnotationPresent(Lob.class)
                ) {
                    String fieldName = LogicalStream
                        .of(f.getAnnotation(Column.class))
                        .ifTrue(c -> c != null && !c.name().isEmpty())
                        .thenReturn(Column::name)
                        .otherwise(c -> f.getName())
                        .get();

                    f.setAccessible(true);
                    Object val = f.get(e);

                    cols.add(fieldName);
                    values.add(val);
                    classes.add(f.getType());
                }
            }

            String condition;
            if (model.getIdField() instanceof MultiFieldIDColumn<?, ?> && id == null) {
                condition = ((MultiFieldIDColumn<E, ?>) model.getIdField()).equal(e).sql();
            } else {
                condition = model.getIdField().equal(ColumnInfo.valueOf(id)).sql();
            }

            String qry = (
                    ConnectionManager.getNaming().doKeywordChange("UPDATE") +
                            " %s " +
                            ConnectionManager.getNaming().doKeywordChange("SET") +
                            " %s " +
                            ConnectionManager.getNaming().doKeywordChange("WHERE") +
                            " %s;"
            ).formatted(
                    table,
                    cols
                        .stream()
                        .map(col -> ConnectionManager.getNaming().doChange(col) + " = ?")
                        .collect(Collectors.joining(", ")),
                    condition
            );

            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            for (int i = 0; i < cols.size(); i++) {
                preparedStatement.setObject(
                        i + 1,
                        values.get(i)
                );
            }

            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "update query",
                    StringHelper.str(preparedStatement),
                    Logger.LogLevel.LOG
                );

            return preparedStatement.executeUpdate();
        } catch (Exception ex) {
            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "error in update query on table " + model.getTable(),
                    ex.toString(),
                    Logger.LogLevel.ERROR
                );

            return -1;
        }
    }

    public static <E> int update(
            Connection connection,
            EntityModel<E, ?> entity,
            SetColumn<?>[] cols, //the target columns to set
            QueryBuilder<E> where //where clause
    ) {
        try {
            String table = ConnectionManager.getNaming().doChange(entity.getTable());

            String qry = (
                    ConnectionManager.getNaming().doKeywordChange("UPDATE") +
                            " %s " +
                            ConnectionManager.getNaming().doKeywordChange("SET") +
                            " %s %s"
            ).formatted(
                    table,
                    Arrays
                            .stream(cols)
                            .map(SetColumn::sql) //(sc.column().column() + " = ?")
                            .collect(Collectors.joining(", ")),
                    LogicalStream
                            .of(where)
                            .ifTrue(Objects::nonNull)
                            .thenReturn(w -> ConnectionManager.getNaming().doKeywordChange("where ") + where.query().sql())
                            .otherwise(w -> "")
                            .get()
            );

            PreparedStatement preparedStatement = connection.prepareStatement(qry);

            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "update query",
                    StringHelper.str(preparedStatement),
                    Logger.LogLevel.LOG
                );

            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "error in update query on table " + entity.getTable(),
                    e.toString(),
                    Logger.LogLevel.LOG
                );

            return -1;
        }
    }

    public static <E> int delete(Connection connection, EntityModel<E, ?> entity, QueryBuilder<E> where) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
            ConnectionManager.getNaming().doKeywordChange("delete from ") +
                    ConnectionManager.getNaming().doChange(entity.getTable()) +
                    LogicalStream
                        .of(where)
                        .ifTrue(Objects::nonNull)
                        .thenReturn(w -> ConnectionManager.getNaming().doKeywordChange(" where ") + where.query().sql())
                        .otherwise(w -> "")
                        .get()
            );

            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "delete query",
                    StringHelper.str(preparedStatement),
                    Logger.LogLevel.LOG
                );

            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            if(ConnectionManager.isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "error in delete query on table " + entity.getTable(),
                    e.toString(),
                    Logger.LogLevel.ERROR
                );

            return -1;
        }
    }

    public static <E, R> List<R> select(
            Statement statement,
            EntityModel<E, ?> entity,
            ColumnInfo<?>[] cols,
            QueryBuilder<E> where,
            Class<R> resultClazz
    ) throws SQLException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return select(
                statement,
                entity,
                null,
                cols,
                where,
                new ColumnInfo[0],
                null,
                new OrderColumn[0],
                -1,
                -1,
                resultClazz
        );
    }

    public static <E> List<E> select(
            Statement statement,
            EntityModel<E,?> entity,
            QueryBuilder<E> where
    ) throws IllegalAccessException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return select(
                statement,
                entity,
                null,
                new ColumnInfo[0],
                where,
                new ColumnInfo[0],
                null,
                new OrderColumn[0],
                -1,
                -1,
                entity.getClazz()
        );
    }


    public static <E, R> List<R> select(
            Statement statement,
            EntityModel<E, ?> entity, //target table
            Join<?, ?> join, //joining
            ColumnInfo<?>[] cols, //the target columns to select
            QueryBuilder<E> where, //where clause,
            ColumnInfo<?>[] groupCols,
            QueryBuilder<E> having,
            OrderColumn<?>[] orderByCols,
            long limit,
            long skip,
            Class<R> resultClazz
    ) throws
        IllegalAccessException, SQLException,
        InvocationTargetException, NoSuchMethodException,
        InstantiationException
    {
        return fetch(
            statement.executeQuery(
                buildSelectQuery(
                    entity, join, cols, where, groupCols, having, orderByCols, limit, skip
                )
            ),
            resultClazz
        );
    }

    public static <E> String buildSelectQuery(
        EntityModel<E, ?> entity, //target table
        Join<?, ?> join, //joining
        ColumnInfo<?>[] cols, //the target columns to select
        QueryBuilder<E> where, //where clause,
        ColumnInfo<?>[] groupCols,
        QueryBuilder<E> having,
        OrderColumn<?>[] orderByCols,
        long limit,
        long skip
    ) {
        Naming naming = ConnectionManager.getNaming();
        String table = entity.sql();

        return ConnectionManager.getDialect().buildLimitOffsetQuery(
            naming.doKeywordChange("SELECT ")

            +

            /* START - SELECT COLUMNS */
            LogicalStream
                .of(cols)
                .ifTrue(c -> c != null && c.length > 0)
                .thenReturn(arr -> Arrays
                    .stream(arr)
                    .map(ColumnInfo::toString)
                    .collect(Collectors.joining(", "))
                )
                .otherwise(s -> "*")
                .get()
            /* END - SELECT COLUMNS */

            +

            naming.doKeywordChange(" FROM ") + (join != null ? join.sql() : table)

            +

            /* START - WHERE CLAUSE */
            LogicalStream
                .of(where)
                .ifTrue(w -> w != null && where.query() != null)
                .thenReturn(w -> naming.doKeywordChange(" WHERE ") + where.query().sql())
                .otherwise(w -> "")
                .get()
            /* END - WHERE CLAUSE */

            +

            /* START - GROUP BY */
            LogicalStream
                .of(groupCols)
                .ifTrue(c -> c != null && c.length > 0)
                .thenReturn(arr -> naming.doKeywordChange(" GROUP BY ") + Arrays
                    .stream(arr)
                    .map(ColumnInfo::groupNaming)
                    .collect(Collectors.joining(", "))
                )
                .otherwise(s -> "")
                .get()
            /* END - GROUP BY */

            +

            /* START - HAVING CLAUSE */
            LogicalStream
                .of(having)
                .ifTrue(w -> w != null && having.query() != null)
                .thenReturn(w -> naming.doKeywordChange(" HAVING ") + having.query().sql())
                .otherwise(w -> "")
                .get()
            /* END - HAVING CLAUSE */

            +

            /* START - ORDER BY */
            LogicalStream
                .of(orderByCols)
                .ifTrue(c -> c != null && c.length > 0)
                .thenReturn(arr -> naming.doKeywordChange(" ORDER BY ") + Arrays
                    .stream(arr)
                    .map(OrderColumn::sql)
                    .collect(Collectors.joining(", "))
                )
                .otherwise(s -> "")
                .get()
            /* END - ORDER BY */

        , limit, skip);
    }
}
