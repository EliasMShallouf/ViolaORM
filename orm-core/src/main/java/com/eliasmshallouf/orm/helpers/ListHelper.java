package com.eliasmshallouf.orm.helpers;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.StreamSupport;

public class ListHelper {
    public static <T> List<T> toList(Iterable<T> iterable) {
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .toList();
    }

    public static <T> T[] arrayOf(List<T> list, Class<T> type) {
        if(list == null)
            return null;

        T[] arr = (T[]) Array.newInstance(type, list.size());

        try {
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arr;
    }
}
