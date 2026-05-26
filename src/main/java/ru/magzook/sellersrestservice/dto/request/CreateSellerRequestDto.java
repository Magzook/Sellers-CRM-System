package ru.magzook.sellersrestservice.dto.request;

public class CreateSellerRequestDto extends SellerRequestDto {
    public CreateSellerRequestDto(String name, String contactInfo) {
        super(name, contactInfo);
    }
}