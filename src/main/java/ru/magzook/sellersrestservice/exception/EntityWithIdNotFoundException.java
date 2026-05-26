package ru.magzook.sellersrestservice.exception;

public abstract class EntityWithIdNotFoundException extends RuntimeException {
    /**
     *
     * @param entityName class-like name, will be converted to lowercase
     * @param id target id
     */
    protected EntityWithIdNotFoundException(String entityName, Object id) {
        super(String.format("%s with id %s not found", entityName.toLowerCase(), id));
    }
}