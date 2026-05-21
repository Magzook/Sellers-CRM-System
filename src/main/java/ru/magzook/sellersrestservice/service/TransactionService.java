package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.magzook.sellersrestservice.dto.request.CreateTransactionRequestDto;
import ru.magzook.sellersrestservice.dto.response.TransactionDto;
import ru.magzook.sellersrestservice.dto.response.TransactionListDto;
import ru.magzook.sellersrestservice.dto.response.TransactionWithSellerDto;
import ru.magzook.sellersrestservice.entity.Seller;
import ru.magzook.sellersrestservice.entity.Transaction;
import ru.magzook.sellersrestservice.exception.EntityWithIdNotFoundException;
import ru.magzook.sellersrestservice.repository.SellerRepository;
import ru.magzook.sellersrestservice.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final SellerService sellerService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository, SellerService sellerService) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
        this.sellerService = sellerService;
    }

    public TransactionListDto findAll() {
        List<TransactionDto> transactions = transactionRepository.findAll().stream()
                .map(Transaction::toDto)
                .toList();
        return new TransactionListDto(transactions);
    }

    public TransactionWithSellerDto findByIdFetchSeller(int id) {
        return transactionRepository.findByIdFetchSeller(id)
                .map(t -> new TransactionWithSellerDto(
                        t.getId(),
                        t.getAmount(),
                        t.getPaymentType(),
                        t.getTransactionDate(),
                        t.getSeller().toDto()
                ))
                .orElseThrow(() -> new EntityWithIdNotFoundException("Transaction", id));
    }

    public TransactionListDto findBySellerId(int sellerId) {
        Seller seller = sellerRepository
                .findByIdFetchTransactions(sellerId)
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", sellerId));
        List<TransactionDto> transactions = seller.getTransactions().stream()
                .map(Transaction::toDto)
                .toList();
        return new TransactionListDto(transactions);
    }

    public TransactionDto create(CreateTransactionRequestDto request) {
        Seller seller = sellerRepository
                .findById(request.getSellerId())
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", request.getSellerId()));

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getPaymentType(),
                LocalDateTime.now(),
                seller
        );
        return transactionRepository.save(transaction).toDto();
    }
}
