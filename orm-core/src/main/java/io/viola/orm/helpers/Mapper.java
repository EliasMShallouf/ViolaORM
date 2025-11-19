package io.viola.orm.helpers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Mapper {
    private static final DateTimeFormatter formatterAll = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static String bytesToHex(byte[] bytes) {
        if(bytes == null)
            return null;

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String mapToSqlValue(Object o) {
        if(o instanceof String)
            return "'" + o + "'";
        else if(o instanceof Date d)
            return "'" + formatterDate.format(d.toInstant()) + "'";
        else if(o instanceof LocalDate d)
            return "'" + formatterDate.format(d) + "'";
        else if(o instanceof LocalTime t)
            return "'" + formatterTime.format(t) + "'";
        else if(o instanceof LocalDateTime d)
            return "'" + formatterAll.format(d) + "'";
        else if(o instanceof byte[] ba)
            return "0x" + bytesToHex(ba);
        else
            return o.toString();
    }

    public static String mapToSqlType(Class<?> c) {
        return SQLTypesMapper.mapToSqlType(c);

        /*if(c == String.class)
            return "text";
        else if(c == Character.class || c == char.class)
            return "varchar";
        else if (c == Integer.class || c == Long.class || c == Short.class || c == int.class || c == long.class || c == short.class)
            return "int";
        else if (c == Double.class || c == Float.class || c == double.class || c == float.class)
            return "double";
        else if (c == Boolean.class || c == boolean.class)
            return "boolean";
        else if (c == Byte.class || c == byte.class)
            return "byte";
        else if (c == LocalDate.class)
            return "date";
        else if (c == LocalTime.class)
            return "time";
        else if (c == LocalDateTime.class || c == Date.class)
            return "datetime";
        else if (c == byte[].class)
            return "blob";
        else
            return "text";*/
    }

    public static Class<?> mapFromPrimitive(Class<?> p) {
        if(p.equals(int.class))
            return Integer.class;
        if(p.equals(double.class))
            return Double.class;
        if(p.equals(float.class))
            return Float.class;
        if(p.equals(short.class))
            return Short.class;
        if(p.equals(long.class))
            return Long.class;
        if(p.equals(boolean.class))
            return Boolean.class;
        if(p.equals(char.class))
            return Character.class;
        if(p.equals(byte.class))
            return Byte.class;
        if(p.equals(Date.class))
            return LocalDateTime.class;

        return p;
    }
}
