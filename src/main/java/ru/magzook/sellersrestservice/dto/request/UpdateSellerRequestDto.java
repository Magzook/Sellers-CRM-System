package ru.magzook.sellersrestservice.dto.request;

public class UpdateSellerRequestDto extends SellerRequestDto {
    public UpdateSellerRequestDto(String name, String contactInfo) {
        super(name, contactInfo);
    }
}