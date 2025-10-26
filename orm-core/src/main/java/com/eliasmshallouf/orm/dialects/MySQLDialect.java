package com.eliasmshallouf.orm.dialects;

import com.eliasmshallouf.orm.helpers.SQLTypesMapper;
import com.eliasmshallouf.orm.naming.Naming;

public class MySQLDialect implements Dialect {
    private final Dialect defaultDialect = defaultDialect();

    @Override
    public String tableCountRowsQuery(String db, String table) {
        return "SELECT TABLE_ROWS FROM INFORMATION_SCHEMA.`TABLES` WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = \"%s\" AND TABLE_NAME = \"%s\"".formatted(
            db, table
        );
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
        return SQLTypesMapper.DbDialect.MYSQL;
    }
}
