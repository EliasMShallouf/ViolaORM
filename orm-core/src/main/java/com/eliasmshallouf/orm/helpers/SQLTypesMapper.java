package com.eliasmshallouf.orm.helpers;

import com.eliasmshallouf.orm.ConnectionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class SQLTypesMapper {
    public enum DbDialect {
        MYSQL,
        POSTGRESQL,
        SQLSERVER,
        ORACLE,
        SQLITE,
        H2
    }

    private static String mapToSqlType(Class<?> c, DbDialect dialect) {
        if (c == String.class) {
            return "TEXT";
        }
        else if (c == Character.class || c == char.class) {
            return dialect == DbDialect.ORACLE ? "CHAR(1)" : "VARCHAR(1)";
        }
        else if (c == Integer.class || c == int.class) {
            switch (dialect) {
                case POSTGRESQL: return "INTEGER";
                case ORACLE: return "NUMBER(10)";
                default: return "INT";
            }
        }
        else if (c == Long.class || c == long.class) {
            switch (dialect) {
                case ORACLE: return "NUMBER(19)";
                default: return "BIGINT";
            }
        }
        else if (c == Short.class || c == short.class) {
            switch (dialect) {
                case ORACLE: return "NUMBER(5)";
                default: return "SMALLINT";
            }
        }
        else if (c == Double.class || c == double.class) {
            switch (dialect) {
                case POSTGRESQL: return "DOUBLE PRECISION";
                case ORACLE: return "BINARY_DOUBLE";
                case SQLSERVER: return "FLOAT(53)";
                default: return "DOUBLE";
            }
        }
        else if (c == Float.class || c == float.class) {
            switch (dialect) {
                case POSTGRESQL: return "REAL";
                case ORACLE: return "BINARY_FLOAT";
                case SQLSERVER: return "FLOAT(24)";
                default: return "FLOAT";
            }
        }
        else if (c == Boolean.class || c == boolean.class) {
            switch (dialect) {
                case ORACLE: return "NUMBER(1)"; // Oracle uses 1/0 for boolean
                case SQLITE: return "INTEGER"; // SQLite uses 0/1
                default: return "BOOLEAN";
            }
        }
        else if (c == Byte.class || c == byte.class) {
            return "TINYINT"; // Most databases use TINYINT for byte
        }
        else if (c == LocalDate.class) {
            return "DATE";
        }
        else if (c == LocalTime.class) {
            switch (dialect) {
                case ORACLE: return "TIMESTAMP";
                default: return "TIME";
            }
        }
        else if (c == LocalDateTime.class || c == Date.class) {
            switch (dialect) {
                case SQLSERVER: return "DATETIME2";
                case MYSQL: return "DATETIME";
                default: return "TIMESTAMP";
            }
        }
        else if (c == byte[].class) {
            switch (dialect) {
                case POSTGRESQL: return "BYTEA";
                case SQLSERVER: return "VARBINARY(MAX)";
                default: return "BLOB";
            }
        }
        else if (c == BigDecimal.class) {
            switch (dialect) {
                case POSTGRESQL: return "NUMERIC";
                case ORACLE: return "NUMBER";
                default: return "DECIMAL";
            }
        }
        else {
            return "TEXT";
        }
    }

    public static String mapToSqlType(Class<?> c) {
        return mapToSqlType(c, ConnectionManager.getDialect().type());
    }
}
