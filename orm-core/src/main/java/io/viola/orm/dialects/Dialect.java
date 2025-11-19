package io.viola.orm.dialects;

import io.viola.orm.naming.Naming;
import io.viola.orm.ConnectionManager;
import io.viola.orm.helpers.SQLTypesMapper;

public interface Dialect {
    String tableCountRowsQuery(String db, String table);

    String buildLimitOffsetQuery(String query, long limit, String offset);

    String removeLimitOffsetFromQuery(String query);

    Naming namingStrategy();

    SQLTypesMapper.DbDialect type();

    default String buildLimitOffsetQuery(String query, long limit, long offset) {
        return buildLimitOffsetQuery(query, limit, offset + "");
    }

    static Dialect defaultDialect() {
        return new Dialect() {
            @Override
            public String tableCountRowsQuery(String db, String table) {
                return null;
            }

            @Override
            public String buildLimitOffsetQuery(String query, long limit, String offset) {
                return
                    query +
                    (limit > 0 ? (" " + ConnectionManager.getNaming().doKeywordChange("limit ") + limit) : "") +
                    ((!offset.isEmpty() && (offset.equals("?") || Long.parseLong(offset) > 0)) ? (" " + ConnectionManager.getNaming().doKeywordChange("offset ") + offset) : "")
                ;
            }

            @Override
            public String removeLimitOffsetFromQuery(String qry) {
                return qry
                    .trim()
                    .replaceAll("((?i)offset ([0-9]+|\\?))$", "")
                    .trim()
                    .replaceAll("((?i)limit [0-9]+)$", "")
                    .trim();
            }

            @Override
            public Naming namingStrategy() {
                return null;
            }

            @Override
            public SQLTypesMapper.DbDialect type() {
                return null;
            }
        };
    }
}
