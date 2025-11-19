package io.viola.orm.transactions;

import io.viola.orm.ConnectionManager;

@FunctionalInterface
public interface TransactionWorker {
    TransactionResult transact(ConnectionManager manager, Transaction transaction);
}
