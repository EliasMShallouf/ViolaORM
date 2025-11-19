package io.viola.orm.transactions;

import io.viola.orm.ConnectionManager;

import java.sql.SQLException;

public class Transaction {
    private final ConnectionManager connectionManager;

    public Transaction(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void start() {
        try {
            connectionManager.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            connectionManager.getConnection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            connectionManager.getConnection().rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            connectionManager.getConnection().setAutoCommit(true);
            connectionManager.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void transact(TransactionWorker worker) {
        TransactionResult result = worker.transact(connectionManager, this);

        if (result == TransactionResult.COMMIT)
            commit();
        else
            rollback();
    }
}
