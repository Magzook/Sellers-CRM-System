package ru.magzook.sellersrestservice.dto.response;

import java.util.List;

public class TransactionListDto {
    private final List<TransactionDto> transactions;

    public TransactionListDto(List<TransactionDto> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionDto> getTransactions() {
        return transactions;
    }
}
