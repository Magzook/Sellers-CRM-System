package ru.magzook.sellersrestservice.dto.response;

import java.util.List;

public class SellerWithTotalListDto {
    private final List<SellerWithTotalDto> sellers;

    public SellerWithTotalListDto(List<SellerWithTotalDto> sellers) {
        this.sellers = sellers;
    }

    public List<SellerWithTotalDto> getSellers() {
        return sellers;
    }
}
