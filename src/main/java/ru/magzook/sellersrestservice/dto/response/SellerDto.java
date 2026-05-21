package ru.magzook.sellersrestservice.dto.response;

import java.time.LocalDateTime;

public class SellerDto {
    private final int id;
    private final String name;
    private final String contactInfo;
    private final LocalDateTime registrationDate;

    public SellerDto(int id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
}
