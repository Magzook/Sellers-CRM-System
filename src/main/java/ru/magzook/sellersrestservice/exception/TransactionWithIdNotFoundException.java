package ru.magzook.sellersrestservice.exception;

public class TransactionWithIdNotFoundException extends EntityWithIdNotFoundException {
    public TransactionWithIdNotFoundException(long id) {
        super("transaction", id);
    }
}
