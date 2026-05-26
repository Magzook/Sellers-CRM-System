package ru.magzook.sellersrestservice.dto.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.magzook.sellersrestservice.dto.request.CreateTransactionRequestDto;
import ru.magzook.sellersrestservice.dto.response.TransactionDto;
import ru.magzook.sellersrestservice.dto.response.TransactionWithSellerDto;
import ru.magzook.sellersrestservice.entity.Seller;
import ru.magzook.sellersrestservice.entity.Transaction;
import java.time.LocalDateTime;

@Component
public class TransactionMapper {
    private final SellerMapper sellerMapper;

    @Autowired
    public TransactionMapper(SellerMapper sellerMapper) {
        this.sellerMapper = sellerMapper;
    }

    public TransactionDto toTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getPaymentType(),
                transaction.getTransactionDate(),
                transaction.getSeller().getId()
        );
    }
    public TransactionWithSellerDto toTransactionWithSellerDto(Transaction transaction) {
        return new TransactionWithSellerDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getPaymentType(),
                transaction.getTransactionDate(),
                sellerMapper.toSellerDto(transaction.getSeller())
        );
    }

    public Transaction toTransaction(CreateTransactionRequestDto dto, Seller seller) {
        return new Transaction(
                dto.getAmount(),
                dto.getPaymentType(),
                LocalDateTime.now(),
                seller
        );
    }
}
