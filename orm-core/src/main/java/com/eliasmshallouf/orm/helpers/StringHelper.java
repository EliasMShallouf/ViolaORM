package com.eliasmshallouf.orm.helpers;

import java.sql.Statement;

public class StringHelper {
    public static String str(Statement statement) {
        String[] stmt = statement.toString().split(":");
        return (stmt.length > 1 ? stmt[1] : stmt[0]).trim();
    }
}
