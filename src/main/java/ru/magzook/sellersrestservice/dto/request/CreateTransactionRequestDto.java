package ru.magzook.sellersrestservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;

import java.math.BigDecimal;

public class CreateTransactionRequestDto {

    @NotNull(message = "amount cannot be null")
    @DecimalMin(value = "0.0", message = "amount cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "amount must fit numeric(14,2)")
    private final BigDecimal amount;

    @NotNull(message = "paymentType cannot be null")
    private final PaymentType paymentType;

    private final int sellerId;

    public CreateTransactionRequestDto(BigDecimal amount, PaymentType paymentType, int sellerId) {
        this.amount = amount;
        this.paymentType = paymentType;
        this.sellerId = sellerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public int getSellerId() {
        return sellerId;
    }
}
