package com.eliasmshallouf.orm.dialects;

import com.eliasmshallouf.orm.helpers.SQLTypesMapper;
import com.eliasmshallouf.orm.naming.Naming;

public class SQLiteDialect implements Dialect {
    private final Dialect defaultDialect = defaultDialect();

    @Override
    public String tableCountRowsQuery(String db, String table) {
        return null;
    }

    @Override
    public String buildLimitOffsetQuery(String query, long limit, String offset) {
        return defaultDialect.buildLimitOffsetQuery(query, limit, offset);
    }

    @Override
    public String removeLimitOffsetFromQuery(String query) {
        return defaultDialect.removeLimitOffsetFromQuery(query);
    }

    @Override
    public Naming namingStrategy() {
        return null;
    }

    @Override
    public SQLTypesMapper.DbDialect type() {
        return SQLTypesMapper.DbDialect.SQLITE;
    }
}
