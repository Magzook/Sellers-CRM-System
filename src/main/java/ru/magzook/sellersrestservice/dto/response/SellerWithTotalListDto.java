package ru.magzook.sellersrestservice.dto.response;

import java.util.List;

public record SellerWithTotalListDto(List<SellerWithTotalDto> sellers) {
}
