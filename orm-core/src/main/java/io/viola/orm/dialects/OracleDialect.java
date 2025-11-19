package io.viola.orm.dialects;

import io.viola.orm.naming.Naming;
import io.viola.orm.ConnectionManager;
import io.viola.orm.helpers.SQLTypesMapper;

public class OracleDialect implements Dialect {
    @Override
    public String tableCountRowsQuery(String db, String table) {
        return "SELECT num_rows " +
                "FROM all_tables " +
                "WHERE owner = '%s' AND table_name = '%s'".formatted(db, table);
    }

    @Override
    public String buildLimitOffsetQuery(String query, long limit, String offset) {
        return
            query +
            ((!offset.isEmpty() && (offset.equals("?") || Long.parseLong(offset) > 0)) ? (" " + ConnectionManager.getNaming().doKeywordChange("OFFSET %s ROWS").formatted(offset)) : "") +
            (limit > 0 ? (" " + ConnectionManager.getNaming().doKeywordChange("FETCH NEXT %s ROWS ONLY").formatted(limit + "")) : "")
        ;
    }

    @Override
    public String removeLimitOffsetFromQuery(String query) {
        return query
            .trim()
            .replaceAll("((?i)fetch next [0-9]+ rows only)$", "")
            .trim()
            .replaceAll("((?i)offset ([0-9]+|\\?) rows)$", "")
            .trim();
    }

    @Override
    public Naming namingStrategy() {
        return null;
    }

    @Override
    public SQLTypesMapper.DbDialect type() {
        return SQLTypesMapper.DbDialect.ORACLE;
    }
}
