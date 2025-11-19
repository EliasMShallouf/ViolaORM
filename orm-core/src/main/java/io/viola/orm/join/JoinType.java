package io.viola.orm.join;

import io.viola.orm.ConnectionManager;

public enum JoinType {
    DEFAULT_JOIN("join"),
    INNER_JOIN("inner join"),
    LEFT_OUTER_JOIN("left outer join"),
    RIGHT_OUTER_JOIN("right outer join"),
    FULL_OUTER_JOIN("full outer join"),
    CROSS_JOIN("cross join")
    ;

    private String sql;

    JoinType(String s) {
        this.sql = s;
    }

    public String sql() {
        return ConnectionManager.getNaming().doKeywordChange(this.sql);
    }
}
