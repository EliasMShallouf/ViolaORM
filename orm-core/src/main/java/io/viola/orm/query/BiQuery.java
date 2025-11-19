package io.viola.orm.query;

import io.viola.orm.ConnectionManager;

public class BiQuery implements Query {
    public enum BiOperator {
        AND, OR
    }

    private Query q1, q2;
    private BiOperator op;

    public BiQuery(Query q1, Query q2, BiOperator op) {
        this.q1 = q1;
        this.q2 = q2;
        this.op = op;
    }

    @Override
    public String sql() {
        return String.format("(%s) %s (%s)",
            q1.sql(),
            ConnectionManager.getNaming().doKeywordChange(op.name().toLowerCase()),
            q2.sql()
        );
    }
}
