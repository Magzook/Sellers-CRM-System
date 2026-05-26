package ru.magzook.sellersrestservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUpdateSellerRequestDto(
        @NotBlank(message = "name cannot be empty")
        @Size(max = 16, message = "name cannot be longer than 16 characters")
        String name,

        @NotBlank(message = "contactInfo cannot be empty")
        String contactInfo) { }
