package com.eliasmshallouf.orm.dialects;

import com.eliasmshallouf.orm.ConnectionManager;
import com.eliasmshallouf.orm.helpers.SQLTypesMapper;
import com.eliasmshallouf.orm.naming.Naming;

public class SQLServerDialect implements Dialect {
    @Override
    public String tableCountRowsQuery(String db, String table) {
        return "SELECT SUM(p.rows) AS row_count " +
                "FROM sys.tables t " +
                "JOIN sys.partitions p ON t.object_id = p.object_id " +
                "WHERE t.name = '%s' AND p.index_id IN (0, 1)".formatted(table);
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
        return SQLTypesMapper.DbDialect.SQLSERVER;
    }
}
