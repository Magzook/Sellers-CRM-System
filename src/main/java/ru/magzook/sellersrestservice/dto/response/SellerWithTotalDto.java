package ru.magzook.sellersrestservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerWithTotalDto(
        int id,
        String name,
        String contactInfo,
        LocalDateTime registrationDate,
        BigDecimal total) { }
