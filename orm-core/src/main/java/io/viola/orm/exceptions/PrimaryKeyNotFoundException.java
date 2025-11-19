package io.viola.orm.exceptions;

public class PrimaryKeyNotFoundException extends RuntimeException {
    public PrimaryKeyNotFoundException(Class<?> entity) {
        super("PrimaryKey for entity (" + entity.getSimpleName() + ") has not found\n try to add an @Id field");
    }
}
