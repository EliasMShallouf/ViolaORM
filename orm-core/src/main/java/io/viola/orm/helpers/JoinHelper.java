package io.viola.orm.helpers;

import io.viola.orm.ConnectionManager;
import io.viola.orm.join.Join;
import io.viola.orm.table.EntityModel;

public class JoinHelper {
    public static <E> String join(E root) {
        if(root instanceof Join j)
            return
                join(j.getLeft()) +
                " " + j.getType().sql() + " " +
                join(j.getRight()) +
                ConnectionManager.getNaming().doKeywordChange(" on ") +
                j.on().sql()
                + " ";

        if(root instanceof EntityModel e)
            return e.sql();

        return "";
    }
}
