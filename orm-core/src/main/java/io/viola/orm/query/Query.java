package io.viola.orm.query;

import io.viola.orm.ConnectionManager;

@FunctionalInterface
public interface Query {
    String sql();

    default BiQuery and(Query q) {
        return new BiQuery(this, q, BiQuery.BiOperator.AND);
    }

    default BiQuery or(Query q) {
        return new BiQuery(this, q, BiQuery.BiOperator.OR);
    }

    default Query not() {
        return () -> String.format(ConnectionManager.getNaming().doKeywordChange("not ") + "(%s)", sql());
    }

    static Query noCondition() {
        return () -> "1 = 1";
    }
}
