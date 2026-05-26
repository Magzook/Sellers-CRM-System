package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.magzook.sellersrestservice.dto.mapping.TransactionMapper;
import ru.magzook.sellersrestservice.dto.request.CreateTransactionRequestDto;
import ru.magzook.sellersrestservice.dto.response.TransactionDto;
import ru.magzook.sellersrestservice.dto.response.TransactionListDto;
import ru.magzook.sellersrestservice.dto.response.TransactionWithSellerDto;
import ru.magzook.sellersrestservice.entity.Seller;
import ru.magzook.sellersrestservice.entity.Transaction;
import ru.magzook.sellersrestservice.exception.EntityWithIdNotFoundException;
import ru.magzook.sellersrestservice.repository.SellerRepository;
import ru.magzook.sellersrestservice.repository.TransactionRepository;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionListDto findAll() {
        List<TransactionDto> transactions = transactionRepository.findAll().stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
        return new TransactionListDto(transactions);
    }

    public TransactionWithSellerDto findByIdFetchSeller(int id) {
        return transactionRepository.findByIdFetchSeller(id)
                .map(transactionMapper::toTransactionWithSellerDto)
                .orElseThrow(() -> new EntityWithIdNotFoundException("Transaction", id));
    }

    public TransactionListDto findBySellerId(int sellerId) {
        Seller seller = sellerRepository
                .findByIdFetchTransactions(sellerId)
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", sellerId));
        List<TransactionDto> transactions = seller.getTransactions().stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
        return new TransactionListDto(transactions);
    }

    public TransactionDto create(CreateTransactionRequestDto request) {
        Seller seller = sellerRepository
                .findById(request.sellerId())
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", request.sellerId()));

        Transaction transaction = transactionMapper.toTransactionEntity(request, seller);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionDto(transaction);
    }
}
