package ru.magzook.sellersrestservice.dto.mapping;

import org.springframework.stereotype.Component;
import ru.magzook.sellersrestservice.dto.request.CreateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.request.UpdateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.response.SellerDto;
import ru.magzook.sellersrestservice.entity.Seller;
import java.time.LocalDateTime;

@Component
public class SellerMapper {
    public SellerDto toSellerDto(Seller seller) {
        return new SellerDto(
                seller.getId(),
                seller.getName(),
                seller.getContactInfo(),
                seller.getRegistrationDate()
        );
    }

    public Seller toSellerEntity(CreateSellerRequestDto dto) {
        return new Seller(
                dto.getName(),
                dto.getContactInfo(),
                LocalDateTime.now()
        );
    }

    public Seller toSellerEntity(UpdateSellerRequestDto dto, int id, LocalDateTime registrationDate) {
        return new Seller(
                id,
                dto.getName(),
                dto.getContactInfo(),
                registrationDate
        );
    }
}
