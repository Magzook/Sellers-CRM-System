package ru.magzook.sellersrestservice.dto.mapping;

import org.springframework.stereotype.Component;
import ru.magzook.sellersrestservice.dto.request.CreateUpdateSellerRequestDto;
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

    public Seller toSellerEntity(CreateUpdateSellerRequestDto dto) {
        return new Seller(
                dto.name(),
                dto.contactInfo(),
                LocalDateTime.now()
        );
    }

    public Seller toSellerEntity(CreateUpdateSellerRequestDto dto, int id, LocalDateTime registrationDate) {
        return new Seller(
                id,
                dto.name(),
                dto.contactInfo(),
                registrationDate
        );
    }
}
