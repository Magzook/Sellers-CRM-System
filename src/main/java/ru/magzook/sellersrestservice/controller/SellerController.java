package ru.magzook.sellersrestservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.magzook.sellersrestservice.dto.request.CreateUpdateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.response.SellerDto;
import ru.magzook.sellersrestservice.dto.response.SellerListDto;
import ru.magzook.sellersrestservice.service.SellerService;

@RestController
@RequestMapping("/api/v1")
public class SellerController {
    private final SellerService sellerService;

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping("/sellers")
    public SellerListDto findAll() {
        return sellerService.findAll();
    }

    @GetMapping("/sellers/{id}")
    public SellerDto findById(@PathVariable int id) {
        return sellerService.findById(id);
    }

    @PostMapping("/sellers")
    public SellerDto create(@RequestBody @Valid CreateUpdateSellerRequestDto request) {
        return sellerService.create(request);
    }

    @PutMapping("/sellers/{id}")
    public SellerDto update(
            @PathVariable int id,
            @RequestBody @Valid CreateUpdateSellerRequestDto request) {
        return sellerService.update(id, request);
    }

    @DeleteMapping("/sellers/{id}")
    public void delete(@PathVariable int id) {
        sellerService.delete(id);
    }
}
