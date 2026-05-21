package ru.magzook.sellersrestservice.dto.request;

import ru.magzook.sellersrestservice.entity.Seller;

import java.time.LocalDateTime;

public class UpdateSellerRequestDto extends SellerRequestDto {

    public UpdateSellerRequestDto(String name, String contactInfo) {
        super(name, contactInfo);
    }

    public Seller toEntity(int id, LocalDateTime registrationDate) {
        return new Seller(
                id,
                getName(),
                getContactInfo(),
                registrationDate
        );
    }
}