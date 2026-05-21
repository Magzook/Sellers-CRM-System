package ru.magzook.sellersrestservice.exception;

public class EntityWithIdNotFoundException extends RuntimeException {

    public EntityWithIdNotFoundException(String entityName, Object id) {
        super(String.format("%s with id %s not found", entityName.toLowerCase(), id));
    }
}