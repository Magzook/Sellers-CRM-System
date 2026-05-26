package ru.magzook.sellersrestservice.dto.response;

import ru.magzook.sellersrestservice.dto.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
        long id,
        BigDecimal amount,
        PaymentType paymentType,
        LocalDateTime transactionDate,
        int sellerId) { }
