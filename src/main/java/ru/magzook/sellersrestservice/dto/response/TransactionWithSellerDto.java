package ru.magzook.sellersrestservice.dto.response;

import ru.magzook.sellersrestservice.dto.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionWithSellerDto {
    private final int id;
    private final BigDecimal amount;
    private final PaymentType paymentType;
    private final LocalDateTime transactionDate;
    private final SellerDto seller;

    public TransactionWithSellerDto(int id, BigDecimal amount, PaymentType paymentType, LocalDateTime transactionDate, SellerDto seller) {
        this.id = id;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
        this.seller = seller;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public SellerDto getSeller() {
        return seller;
    }
}
