package ru.magzook.sellersrestservice.dto.request;

import ru.magzook.sellersrestservice.entity.Seller;

import java.time.LocalDateTime;

public class CreateSellerRequestDto extends SellerRequestDto {
    public CreateSellerRequestDto(String name, String contactInfo) {
        super(name, contactInfo);
    }

    public Seller toEntity() {
        return new Seller(
                getName(),
                getContactInfo(),
                LocalDateTime.now()
        );
    }
}