package ru.magzook.sellersrestservice.dto.response;

import ru.magzook.sellersrestservice.dto.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionWithSellerDto(
        int id,
        BigDecimal amount,
        PaymentType paymentType,
        LocalDateTime transactionDate,
        SellerDto seller) { }
