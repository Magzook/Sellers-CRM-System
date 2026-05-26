package ru.magzook.sellersrestservice.dto.response;

import java.time.LocalDateTime;

public record SellerDto(int id, String name, String contactInfo, LocalDateTime registrationDate) {
}
