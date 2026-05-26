package ru.magzook.sellersrestservice.dto.response;

import java.util.List;

public record TransactionListDto(List<TransactionDto> transactions) {
}
