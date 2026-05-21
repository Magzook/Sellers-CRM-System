package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.magzook.sellersrestservice.entity.Seller;
import ru.magzook.sellersrestservice.dto.request.CreateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.request.UpdateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.response.SellerDto;
import ru.magzook.sellersrestservice.dto.response.SellerListDto;
import ru.magzook.sellersrestservice.exception.EntityWithIdNotFoundException;
import ru.magzook.sellersrestservice.repository.SellerRepository;

import java.util.List;

@Service
public class SellerService {
    private final SellerRepository sellerRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public SellerListDto findAll() {
        List<SellerDto> sellers = sellerRepository.findAll().stream()
                .map(Seller::toDto)
                .toList();
        return new SellerListDto(sellers);
    }

    public SellerDto findById(int id) {
        return sellerRepository.findById(id)
                .map(Seller::toDto)
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", id));
    }

    public SellerDto create(CreateSellerRequestDto request) {
        Seller seller = request.toEntity();
        return sellerRepository.save(seller).toDto();
    }

    public SellerDto update(int id, UpdateSellerRequestDto request) {
        return sellerRepository.findById(id)
                .map(oldSeller -> {
                    Seller updatedSeller = request.toEntity(oldSeller.getId(), oldSeller.getRegistrationDate());
                    return sellerRepository.save(updatedSeller).toDto();
                })
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", id));
    }

    public void delete(int id) {
        sellerRepository.deleteById(id);
    }
}
