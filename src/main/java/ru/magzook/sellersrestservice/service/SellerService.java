package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.magzook.sellersrestservice.dto.mapping.SellerMapper;
import ru.magzook.sellersrestservice.entity.Seller;
import ru.magzook.sellersrestservice.dto.request.CreateUpdateSellerRequestDto;
import ru.magzook.sellersrestservice.dto.response.SellerDto;
import ru.magzook.sellersrestservice.dto.response.SellerListDto;
import ru.magzook.sellersrestservice.exception.SellerWithIdNotFoundException;
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
                .orElseThrow(() -> new SellerWithIdNotFoundException(id));
    }

    public SellerDto create(CreateUpdateSellerRequestDto dto) {
        Seller seller = sellerMapper.toSellerEntity(dto);
        seller = sellerRepository.save(seller);
        return sellerMapper.toSellerDto(seller);
    }

    public SellerDto update(int id, CreateUpdateSellerRequestDto dto) {
        return sellerRepository.findById(id)
                .map(oldSeller -> {
                    Seller updatedSeller = sellerMapper
                            .toSellerEntity(dto, oldSeller.getId(), oldSeller.getRegistrationDate());
                    updatedSeller = sellerRepository.save(updatedSeller);
                    return sellerMapper.toSellerDto(updatedSeller);
                })
                .orElseThrow(() -> new SellerWithIdNotFoundException(id));
    }

    public void delete(int id) {
        sellerRepository.deleteById(id);
    }
}
