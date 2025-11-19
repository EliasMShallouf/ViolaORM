package io.viola.orm.dialects;

import java.util.HashMap;
import java.util.Map;

public class DialectFactory {
    private static final Map<String, Dialect> dialectMap = new HashMap<>();

    static {
        dialectMap.put("mysql", new MySQLDialect());
        dialectMap.put("postgresql", new PostgreSQLDialect());
        dialectMap.put("sqlserver", new SQLServerDialect()); // SQL Server 2012+
        dialectMap.put("oracle", new OracleDialect()); // Oracle 12c+
        dialectMap.put("h2", new H2Dialect());
        dialectMap.put("sqlite", new SQLiteDialect());
    }

    public static void registerDialect(String dbType, Dialect dialect) {
        dialectMap.put(dbType, dialect);
    }

    public static Dialect findDialectForURL(String url) {
        url = url.substring("jdbc:".length());
        url = url.substring(0, url.indexOf(":"));

        return dialectMap.get(url);
    }
}
