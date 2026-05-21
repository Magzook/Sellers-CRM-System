package ru.magzook.sellersrestservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public abstract class SellerRequestDto {

    @NotBlank(message = "name cannot be empty")
    @Size(max = 16, message = "name cannot be longer than 16 characters")
    private final String name;

    @NotBlank(message = "contactInfo cannot be empty")
    private final String contactInfo;

    public SellerRequestDto(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }
}
