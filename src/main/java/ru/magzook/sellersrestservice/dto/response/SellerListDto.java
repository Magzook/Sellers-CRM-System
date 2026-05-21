package ru.magzook.sellersrestservice.dto.response;

import java.util.List;

public class SellerListDto {
    private final List<SellerDto> sellers;

    public SellerListDto(List<SellerDto> sellers) {
        this.sellers = sellers;
    }

    public List<SellerDto> getSellers() {
        return sellers;
    }
}
