package ru.magzook.sellersrestservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellerWithTotalDto {
    private int id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;
    private BigDecimal total;

    public SellerWithTotalDto(int id, String name, String contactInfo, LocalDateTime registrationDate, BigDecimal total) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
        this.total = total;
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

    public BigDecimal getTotal() {
        return total;
    }
}
