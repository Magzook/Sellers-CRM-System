package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.magzook.sellersrestservice.dto.mapping.SellerMapper;
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
    private final SellerMapper sellerMapper;

    @Autowired
    public SellerService(SellerRepository sellerRepository, SellerMapper sellerMapper) {
        this.sellerRepository = sellerRepository;
        this.sellerMapper = sellerMapper;
    }

    public SellerListDto findAll() {
        List<SellerDto> sellers = sellerRepository.findAll().stream()
                .map(sellerMapper::toSellerDto)
                .toList();
        return new SellerListDto(sellers);
    }

    public SellerDto findById(int id) {
        return sellerRepository.findById(id)
                .map(sellerMapper::toSellerDto)
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", id));
    }

    public SellerDto create(CreateSellerRequestDto request) {
        Seller seller = sellerMapper.toSellerEntity(request);
        seller = sellerRepository.save(seller);
        return sellerMapper.toSellerDto(seller);
    }

    public SellerDto update(int id, UpdateSellerRequestDto request) {
        return sellerRepository.findById(id)
                .map(oldSeller -> {
                    Seller updatedSeller = sellerMapper.toSellerEntity(request, oldSeller.getId(), oldSeller.getRegistrationDate());
                    updatedSeller = sellerRepository.save(updatedSeller);
                    return sellerMapper.toSellerDto(updatedSeller);
                })
                .orElseThrow(() -> new EntityWithIdNotFoundException("Seller", id));
    }

    public void delete(int id) {
        sellerRepository.deleteById(id);
    }
}
