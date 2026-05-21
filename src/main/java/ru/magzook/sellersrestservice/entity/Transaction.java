package ru.magzook.sellersrestservice.entity;

import jakarta.persistence.*;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;
import ru.magzook.sellersrestservice.dto.response.TransactionDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    public Transaction() {}

    public Transaction(BigDecimal amount, PaymentType paymentType, LocalDateTime transactionDate, Seller seller) {
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
        this.seller = seller;
    }

    public Transaction(int id, BigDecimal amount, PaymentType paymentType, LocalDateTime transactionDate, Seller seller) {
        this.id = id;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
        this.seller = seller;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public TransactionDto toDto() {
        return new TransactionDto(
                id,
                amount,
                paymentType,
                transactionDate,
                seller.getId()
        );
    }
}
