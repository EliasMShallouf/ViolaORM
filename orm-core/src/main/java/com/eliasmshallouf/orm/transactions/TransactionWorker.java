package com.eliasmshallouf.orm.transactions;

import com.eliasmshallouf.orm.ConnectionManager;

@FunctionalInterface
public interface TransactionWorker {
    TransactionResult transact(ConnectionManager manager, Transaction transaction);
}
