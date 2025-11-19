package io.viola.orm.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class ClassHelper {
    public static <E> E createObject(Class<E> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <E> boolean isPrimitive(Class<E> clazz) {
        return Arrays.asList(
            Integer.class,
            Double.class,
            Long.class,
            Boolean.class,
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            LocalTime.class,
            Float.class,
            String.class,
            Byte.class,
            Short.class,
            byte[].class,
            Character.class,
            int.class,
            short.class,
            long.class,
            double.class,
            float.class,
            char.class,
            boolean.class,
            byte.class,
            UUID.class
        ).contains(clazz);
    }

    public static boolean isImplements(Class<?> c, Class<?> i) {
        for(Class<?> tmp : c.getInterfaces())
            if(tmp.equals(i))
                return true;

        return false;
    }

    @SafeVarargs
    public static <V> Map<String, V> mapOf(AbstractMap.SimpleEntry<String, V> ...entries) {
        Map<String, V> map = new HashMap<>();

        for (AbstractMap.SimpleEntry<String, V> entry : entries)
            map.put(entry.getKey(), entry.getValue());

        return map;
    }
}
