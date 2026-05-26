package ru.magzook.sellersrestservice.exception;

public class TransactionWithIdNotFoundException extends EntityWithIdNotFoundException {
    public TransactionWithIdNotFoundException(int id) {
        super("transaction", id);
    }
}
