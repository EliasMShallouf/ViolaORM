package io.viola.orm.dialects;

import io.viola.orm.naming.Naming;
import io.viola.orm.helpers.SQLTypesMapper;

public class PostgreSQLDialect implements Dialect {
    private final Dialect defaultDialect = Dialect.defaultDialect();

    @Override
    public String tableCountRowsQuery(String db, String table) {
        return "SELECT reltuples AS estimate_count " +
                "FROM pg_class " +
                "WHERE relname = '%s'".formatted(table.toLowerCase());
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
        return new Naming() {
            @Override
            public NamingFunction wrapStrategy() {
                return s -> s.contains(" ") ? "\"%s\"".formatted(s) : s;
            }

            @Override
            public NamingFunction namingStrategy() {
                return String::toLowerCase;
            }

            @Override
            public NamingFunction sqlKeywordsNamingStrategy() {
                return String::toLowerCase;
            }
        };
    }

    @Override
    public SQLTypesMapper.DbDialect type() {
        return SQLTypesMapper.DbDialect.POSTGRESQL;
    }
}
