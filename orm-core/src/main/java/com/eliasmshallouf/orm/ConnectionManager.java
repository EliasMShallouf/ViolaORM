package com.eliasmshallouf.orm;

import com.eliasmshallouf.orm.dialects.Dialect;
import com.eliasmshallouf.orm.dialects.DialectFactory;
import com.eliasmshallouf.orm.logger.Logger;
import com.eliasmshallouf.orm.naming.Naming;
import com.eliasmshallouf.orm.paging.Paging;
import com.eliasmshallouf.orm.query.SelectQuery;
import com.eliasmshallouf.orm.table.EntityManager;
import com.eliasmshallouf.orm.transactions.Transaction;
import com.eliasmshallouf.orm.transactions.TransactionWorker;
import com.eliasmshallouf.orm.table.EntityModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {
    private static Dialect dialect = Dialect.defaultDialect();
    private static Naming naming = Naming.defaults();
    private static Logger logger = Logger.noLogger;

    private final Connection connection;
    private final Statement statement;

    private final String driver;
    private final String url;
    private final String user;
    private final String pass;

    public ConnectionManager(
        String driver,
        String url,
        String user,
        String password
    ) {
        this(driver, url, user, password, true);
    }

    private ConnectionManager(
        String driver,
        String url,
        String user,
        String password,
        boolean initDialect
    ) {
        try {
            this.connection = NativeConnectionHelper.createConnection(driver, url, user, password);
            this.statement = this.connection.createStatement();

            if (initDialect)
                setupDialect(url);

            this.driver = driver;
            this.url = url;
            this.user = user;
            this.pass = password;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupDialect(String url) {
        dialect = DialectFactory.findDialectForURL(url);

        if(dialect.namingStrategy() != null)
            naming = dialect.namingStrategy(); //Naming.merge(naming, dialect.namingStrategy());
    }

    public <E> List<E> rawQuery(String qry, Class<E> clazz) {
        try {
            if(isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "rawQuery",
                    qry,
                    Logger.LogLevel.LOG
                );

            return NativeConnectionHelper.fetch(statement.executeQuery(qry), clazz);
        } catch (Exception e) {
            if(isLogEnabled())
                logger.printLog("Error in execute raw query", "\"%s\"".formatted(qry) + ",\n" + e.getMessage(), Logger.LogLevel.ERROR);

            return new ArrayList<>();
        }
    }

    public <E> E findOne(String qry, Class<E> clazz) {
        try {
            if(isLogEnabled())
                ConnectionManager.getLogger().printLog(
                    "findOne query",
                    qry,
                    Logger.LogLevel.LOG
                );

            return NativeConnectionHelper.fetchOne(statement.executeQuery(qry), clazz);
        } catch (Exception e) {
            if(isLogEnabled())
                logger.printLog("Error in execute raw query", "\"%s\"".formatted(qry) + ",\n" + e.getMessage(), Logger.LogLevel.ERROR);

            return null;
        }
    }

    public <E> Paging<E> paging(String qry, int pageSize, boolean lazyFetch, Class<E> clazz) {
        try {
            return new Paging<>(
                this,
                clazz,
                qry,
                pageSize,
                lazyFetch
            );
        } catch (Exception e) {
            throw new RuntimeException(new Exception("Error in execute raw query \"%s\"".formatted(qry) + ",\n" + e.getMessage()));
        }
    }

    public <E> SelectQuery<E> query() {
        return new SelectQuery<>(this);
    }

    public <E, Id> EntityManager<E, Id> createEntityManager(EntityModel<E, Id> model) {
        return new EntityManager<>(this, model);
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public static Dialect getDialect() {
        return dialect;
    }

    public static Naming getNaming() {
        return naming;
    }

    public static void setNaming(Naming naming) {
        ConnectionManager.naming = naming;
    }

    public static void setLogger(Logger logger) {
        ConnectionManager.logger = logger;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean isLogEnabled() {
        return getLogger() != Logger.noLogger;
    }

    public String tableCountRowsQuery(String table) {
        return dialect.tableCountRowsQuery(getDatabaseName(), table);
    }

    private String getDatabaseName() {
        try {
            return connection.getCatalog();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager fork() {
        return new ConnectionManager(
            this.driver,
            this.url,
            this.user,
            this.pass,
            false
        );
    }

    public void transaction(TransactionWorker worker) {
        Transaction transaction = new Transaction(fork());
        transaction.start();
        transaction.transact(worker);
        transaction.stop();
    }

    public Transaction startTransaction() {
        Transaction transaction = new Transaction(fork());
        transaction.start();
        return transaction;
    }
}
