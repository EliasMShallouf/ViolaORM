package com.eliasmshallouf.orm.join;

import com.eliasmshallouf.orm.helpers.JoinHelper;
import com.eliasmshallouf.orm.query.Query;

public class Join<E1, E2> {
    private E1 left;
    private E2 right;
    private JoinType type;
    private JoinQuery<E1, E2> on;

    public Join(E1 left, E2 right, JoinType type, JoinQuery<E1, E2> on) {
        this.left = left;
        this.right = right;
        this.type = type;
        this.on = on;
    }

    public E1 getLeft() {
        return left;
    }

    public E2 getRight() {
        return right;
    }

    public Query on() {
        return on.join(left, right);
    }

    public <E3> Join<Join<E1, E2>, E3> join(E3 e3, JoinType type, JoinQuery<Join<E1, E2>, E3> on) {
        return new Join<>(this, e3, type, on);
    }

    public JoinType getType() {
        return type;
    }

    public String sql() {
        return JoinHelper.join(this);
    }
}
