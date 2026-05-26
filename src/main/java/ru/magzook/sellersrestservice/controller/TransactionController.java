package ru.magzook.sellersrestservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.magzook.sellersrestservice.dto.request.CreateTransactionRequestDto;
import ru.magzook.sellersrestservice.dto.response.TransactionDto;
import ru.magzook.sellersrestservice.dto.response.TransactionListDto;
import ru.magzook.sellersrestservice.dto.response.TransactionWithSellerDto;
import ru.magzook.sellersrestservice.service.TransactionService;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public TransactionListDto findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/transactions/{id}")
    public TransactionWithSellerDto findById(@PathVariable long id) {
        return transactionService.findByIdFetchSeller(id);
    }

    @GetMapping("/sellers/{id}/transactions")
    public TransactionListDto findBySellerId(@PathVariable int id) {
        return transactionService.findBySellerId(id);
    }

    @PostMapping("/transactions")
    public TransactionDto create(@RequestBody @Valid CreateTransactionRequestDto request) {
        return transactionService.create(request);
    }
}
