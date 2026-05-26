package ru.magzook.sellersrestservice.exception;

public class SellerWithIdNotFoundException extends EntityWithIdNotFoundException {
    public SellerWithIdNotFoundException(int id) {
        super("seller", id);
    }
}
