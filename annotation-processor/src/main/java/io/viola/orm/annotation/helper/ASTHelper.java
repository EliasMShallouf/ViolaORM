package io.viola.orm.annotation.helper;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

public class ASTHelper {
    public static boolean isAnnotationPresent(Element e, Class annotation) {
        return e.getAnnotation(annotation) != null;
    }

    public static Class classOf(Element e) {
        TypeMirror type = e.asType();

        if(type.getKind().isPrimitive()) {
            return switch(type.getKind()) {
                case BOOLEAN -> boolean.class;
                case BYTE -> byte.class;
                case SHORT -> short.class;
                case INT -> int.class;
                case LONG -> long.class;
                case CHAR -> char.class;
                case FLOAT -> float.class;
                case DOUBLE -> double.class;

                default -> throw new RuntimeException("Primitive class of type " + type.toString() + " not found!");
            };
        } else {
            if (type.toString().equals("byte[]"))
                return byte[].class;
        }

        try {
            return Class.forName(type.toString());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    // Method to get the declared fields of a class with its parent fields also (recursive)
    public static List<Element> getFieldsOf(TypeElement element, Elements elements) {
        List<Element> res = new ArrayList<>();

        while (!(element.getSuperclass() instanceof NoType)) {
            res.addAll(element.getEnclosedElements());

            element = elements.getTypeElement(element.getSuperclass().toString());
        }

        return res;
    }
}
